from datetime import datetime, timezone
from typing import List, Optional

import numpy as np
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

app = FastAPI(title="KINEDICAL Smart Recommendation & AI Diagnostics API")


# --- DATA MODELS ---

class Item(BaseModel):
    item_id: str = Field(..., description="Mã định danh duy nhất của bài viết y khoa")
    content: str = Field(..., description="Nội dung văn bản chi tiết của bài viết")
    views: int = Field(0, description="Số lượt xem bài viết")
    likes: int = Field(0, description="Số lượt thích bài viết")
    saves: int = Field(0, description="Số lượt lưu/đánh dấu bài viết")
    published_at: Optional[str] = Field(None, description="Ngày xuất bản ở định dạng ISO")
    vector: List[float] = Field(default_factory=list, description="Vector nhúng (Embedding) của bài viết")


class RecommendRequest(BaseModel):
    user_id: str = Field(..., description="ID của người dùng hiện tại")
    items: List[Item] = Field(..., description="Danh sách các bài viết y khoa cần chấm điểm xếp hạng")
    user_vector: List[float] = Field(default_factory=list, description="Vector biểu diễn sở thích cá nhân hóa của người dùng")
    history_vectors: List[List[float]] = Field(default_factory=list, description="Danh sách các vector của các bài viết người dùng đã tương tác trong quá khứ")


class RecommendItem(BaseModel):
    item_id: str
    score: float


class RecommendResponse(BaseModel):
    user_id: str
    recommendations: List[RecommendItem]


class SymptomRequest(BaseModel):
    symptoms: str = Field(..., description="Văn bản mô tả triệu chứng của bệnh nhân")


class PredictResponse(BaseModel):
    predicted_disease: str = Field(..., description="Tên bệnh lý dự đoán dựa trên triệu chứng lâm sàng")
    confidence_score: float = Field(..., description="Độ tin cậy của chẩn đoán (từ 0.0 đến 1.0)")
    ai_recommendations: List[str] = Field(..., description="Lộ trình và lời khuyên chăm sóc sức khỏe cụ thể từ AI")


# --- HELPER FUNCTIONS ---

def parse_publish_date(date_str: Optional[str]) -> datetime:
    """
    Phân tích chuỗi ISO ngày tháng và ĐẢM BẢO luôn trả về đối tượng datetime có múi giờ (aware)
    nhằm tránh lỗi TypeError khi trừ hai kiểu datetime lệch múi giờ.
    """
    if not date_str:
        return datetime.now(timezone.utc)

    try:
        dt = datetime.fromisoformat(date_str)
    except ValueError:
        try:
            # Fallback cho định dạng ISO cũ/chứa ký tự lệch
            dt = datetime.strptime(date_str, "%Y-%m-%dT%H:%M:%S%z")
        except ValueError:
            return datetime.now(timezone.utc)

    # Nếu datetime là naive (không chứa múi giờ), chủ động gán múi giờ UTC
    if dt.tzinfo is None:
        dt = dt.replace(tzinfo=timezone.utc)
    return dt


def normalize(scores: np.ndarray) -> np.ndarray:
    """
    Chuẩn hóa Min-Max mảng điểm số về miền [0.0, 1.0].
    Đảm bảo an toàn kiểu dữ liệu đầu vào và tránh crash khi mảng rỗng hoặc chứa giá trị đồng nhất.
    """
    scores = np.asarray(scores, dtype=float)
    if scores.size == 0:
        return np.array([], dtype=float)
    
    min_val = float(scores.min())
    max_val = float(scores.max())
    
    if max_val <= min_val:
        return np.zeros_like(scores, dtype=float)
    
    return (scores - min_val) / (max_val - min_val)


# --- CORE ALGORITHMIC FUNCTIONS ---

def compute_popularity_scores(items: List[Item]) -> np.ndarray:
    """
    Tính toán điểm phổ biến (Popularity Score) dựa trên lượt xem, thích, lưu và thời gian xuất bản.
    Sử dụng công thức suy giảm liên tục (Continuous decay) để tránh lỗi chia cho 0.
    """
    if not items:
        return np.array([], dtype=float)

    views = np.array([item.views for item in items], dtype=float)
    likes = np.array([item.likes for item in items], dtype=float)
    saves = np.array([item.saves for item in items], dtype=float)

    now = datetime.now(timezone.utc)
    
    # Tính tuổi bài viết chính xác theo giây (float) thay vì số ngày (discrete integer)
    ages_seconds = np.array([
        (now - parse_publish_date(item.published_at)).total_seconds()
        for item in items
    ], dtype=float)
    
    # Đổi sang đơn vị ngày. Nếu bài viết ở tương lai (do lệch giờ hệ thống), gán tuổi tối thiểu = 0.0 ngày
    ages_days = np.maximum(0.0, ages_seconds / 86400.0)

    # Áp dụng công thức suy giảm liên tục để đảm bảo recency luôn > 0 và không bao giờ chia cho 0
    recency = 1.0 / (1.0 + ages_days)

    # Chuẩn hóa logarith để giảm ảnh hưởng của bài viết quá đột biến lượt xem (outliers)
    views_norm = normalize(np.log1p(views))
    likes_norm = normalize(np.log1p(likes))
    saves_norm = normalize(np.log1p(saves))
    recency_norm = normalize(recency)

    # Áp dụng trọng số ưu tiên: Lượt xem (40%), Thích (30%), Lưu (20%), Độ mới (10%)
    weights = np.array([0.40, 0.30, 0.20, 0.10], dtype=float)
    combined = np.vstack([views_norm, likes_norm, saves_norm, recency_norm]).T
    return combined.dot(weights)


def compute_content_scores(request: RecommendRequest) -> np.ndarray:
    """
    Tính điểm tương đồng nội dung (Content-based Filtering) giữa sở thích của người dùng và các bài viết.
    Đã bổ sung cơ chế tự vệ chống lỗi kích thước ma trận và xử lý từ dừng tiếng Việt.
    """
    if not request.items:
        return np.array([], dtype=float)

    # Phương pháp 1: Sử dụng vector nhúng (Embeddings) - Khuyên dùng
    if request.user_vector and any(item.vector for item in request.items):
        vector_dim = len(request.user_vector)
        
        # Đảm bảo phòng ngừa lỗi ma trận lệch chiều: nếu bài viết thiếu vector, lấp đầy bằng vector 0
        item_vectors = np.array([
            item.vector if (item.vector and len(item.vector) == vector_dim) else [0.0] * vector_dim
            for item in request.items
        ], dtype=float)
        
        user_vec = np.array(request.user_vector, dtype=float).reshape(1, -1)
        return cosine_similarity(item_vectors, user_vec).flatten()

    # Phương pháp 2: Fallback sử dụng TF-IDF phân tích từ khóa khi chưa số hóa vector nhúng
    documents = [item.content or "" for item in request.items]
    
    # Sử dụng tập từ dừng tiếng Việt cơ bản để nâng cao độ chính xác trích xuất từ khóa
    vi_stop_words = ["và", "của", "là", "bị", "được", "trong", "cho", "với", "các", "những", "này", "theo", "đã", "đang"]
    
    try:
        vectorizer = TfidfVectorizer(stop_words=vi_stop_words)
        tfidf = vectorizer.fit_transform(documents)
        if tfidf.shape[1] == 0:
            return np.zeros(len(request.items), dtype=float)
        
        # Điểm tương đồng: So sánh với trung bình hóa (centroid)
        centroid = tfidf.mean(axis=0)
        centroid_arr = np.asarray(centroid).reshape(1, -1)
        return cosine_similarity(tfidf, centroid_arr).flatten()
    except ValueError:
        # Xử lý trường hợp tập văn bản trống/không khớp từ vựng
        return np.zeros(len(request.items), dtype=float)


def compute_cf_scores(request: RecommendRequest) -> np.ndarray:
    """
    Tính điểm Lọc cộng tác (Collaborative Filtering) dựa trên lịch sử đọc bài của người dùng.
    Ngăn chặn hoàn toàn lỗi ValueErrors do thiếu vector nhúng bằng cách kiểm chứng kích thước chiều (dimensions).
    """
    if not request.items:
        return np.array([], dtype=float)
    if not request.history_vectors or not any(request.history_vectors):
        return np.zeros(len(request.items), dtype=float)

    # Loại bỏ các vector lịch sử trống
    valid_history = [v for v in request.history_vectors if v and len(v) > 0]
    if not valid_history:
        return np.zeros(len(request.items), dtype=float)

    # Nhận dạng số chiều chuẩn từ vector lịch sử đầu tiên
    vector_dim = len(valid_history[0])

    # Chuẩn hóa kích thước ma trận đầu vào, padding 0.0 cho các vector thiếu/sai chiều
    item_vectors = np.array([
        item.vector if (item.vector and len(item.vector) == vector_dim) else [0.0] * vector_dim
        for item in request.items
    ], dtype=float)
    
    history_matrix = np.array([
        v if len(v) == vector_dim else [0.0] * vector_dim
        for v in valid_history
    ], dtype=float)

    if item_vectors.size == 0 or history_matrix.size == 0:
        return np.zeros(len(request.items), dtype=float)

    # Tính cosine similarity giữa danh sách đề xuất và lịch sử tương tác
    cf_matrix = cosine_similarity(item_vectors, history_matrix)
    # Lấy điểm trung bình tương đồng của từng bài viết với toàn bộ lịch sử tương tác
    return cf_matrix.mean(axis=1)


def compute_hybrid_scores(request: RecommendRequest, alpha: float = 0.7) -> np.ndarray:
    """
    Điểm Gợi ý Lai (Hybrid Score): Kết hợp Content-based (alpha) và Collaborative Filtering (1-alpha).
    """
    alpha = float(np.clip(alpha, 0.0, 1.0))
    content_scores = compute_content_scores(request)
    cf_scores = compute_cf_scores(request)
    
    content_norm = normalize(content_scores)
    cf_norm = normalize(cf_scores)
    
    return alpha * content_norm + (1.0 - alpha) * cf_norm


# --- ENDPOINTS ---

@app.post("/recommend", response_model=RecommendResponse)
@app.post("/ai/recommend", response_model=RecommendResponse)
def recommend(request: RecommendRequest, alpha: float = 0.7):
    """
    API tiếp nhận triệu chứng của bệnh nhân, trả về gợi ý lộ trình y tế được cá nhân hóa cao.
    Đã bổ sung cơ chế tự động xây dựng vector sở thích ảo (Surrogate user vector) từ lịch sử để tránh mất mát dữ liệu.
    """
    try:
        if not request.items:
            raise HTTPException(status_code=400, detail="items must not be empty")

        # NÂNG CẤP THUẬT TOÁN: Nếu người dùng có lịch sử đọc bài nhưng chưa thiết lập user_vector cá nhân,
        # ta tính vector sở thích ảo bằng trung bình cộng (centroid) của các bài viết đã tương tác.
        if not request.user_vector and request.history_vectors:
            valid_history = [v for v in request.history_vectors if v and len(v) > 0]
            if valid_history:
                request.user_vector = list(np.mean(valid_history, axis=0))

        # Nếu hoàn toàn không có dữ liệu cá nhân (User mới/Ẩn danh) -> Gợi ý theo độ phổ biến và độ mới
        if not request.user_vector:
            scores = compute_popularity_scores(request.items)
        else:
            scores = compute_hybrid_scores(request, alpha=alpha)

        # Trả về kết quả đã chấm điểm
        scored_items = [
            RecommendItem(item_id=item.item_id, score=float(score))
            for item, score in zip(request.items, scores)
        ]

        # Sắp xếp và lấy Top 5 bài viết có điểm số cao nhất
        top_items = sorted(scored_items, key=lambda x: x.score, reverse=True)[:5]
        return RecommendResponse(user_id=request.user_id, recommendations=top_items)

    except HTTPException:
        raise
    except Exception as exc:
        raise HTTPException(status_code=500, detail=f"Recommendation processing failed: {exc}")


# --- AI DIAGNOSTICS & SYMPTOM CHECKER ---

def analyze_symptoms_and_predict(symptoms_text: str) -> PredictResponse:
    """
    Thuật toán phân tích triệu chứng y học và chẩn đoán lâm sàng bằng bảng điểm trọng số (Weighted Scorecard)
    kết hợp xử lý cụm từ phủ định (Negation Processing) để tránh việc chẩn đoán sai lệch nghiêm trọng.
    """
    text = symptoms_text.lower()
    
    # 1. Định nghĩa các nhóm từ khóa đại diện cho các chuyên khoa/bệnh lý
    respiratory_kws = ["sốt", "fever", "ho", "cough", "sổ mũi", "runny nose", "đau họng", "sore throat"]
    pneumonia_kws = ["khó thở", "short of breath", "đau ngực", "chest pain"] # Triệu chứng viêm phổi/đường hô hấp nặng
    cardio_kws = ["đau ngực", "chest pain", "nhói ngực", "tim đập nhanh", "palpitation", "hụt hơi"]
    gastro_kws = ["đau bụng", "stomachache", "đau dạ dày", "tiêu chảy", "diarrhea", "buồn nôn", "nôn", "nausea", "vomit", "đầy hơi"]
    neuro_kws = ["đau đầu", "headache", "chóng mặt", "dizzy", "mất ngủ", "insomnia", "stress", "căng thẳng", "lo âu"]

    # 2. Cơ chế đếm điểm có lọc phủ định (Negation-aware Scoring)
    # Nếu trước triệu chứng có từ phủ định như "không", "chưa", "không bị" trong phạm vi 15 ký tự -> Điểm số sẽ bị khấu trừ
    def calculate_score(keywords: List[str]) -> float:
        score = 0.0
        for kw in keywords:
            if kw in text:
                pos = text.find(kw)
                # Trích xuất đoạn văn bản ngắn ngay trước từ khóa
                prefix = text[max(0, pos - 15):pos]
                if any(neg in prefix for neg in ["không", "chưa", "không bị", "bình thường"]):
                    # Triệu chứng bị phủ định -> Trừ điểm hoặc không cộng điểm
                    score -= 0.5
                else:
                    score += 1.0
        return score

    # Tính điểm cho từng nhóm chuyên khoa
    respiratory_score = calculate_score(respiratory_kws)
    cardio_score = calculate_score(cardio_kws)
    gastro_score = calculate_score(gastro_kws)
    neuro_score = calculate_score(neuro_kws)
    
    # Điểm đánh giá viêm phổi (khó thở/đau ngực xuất hiện cùng nhóm hô hấp)
    pneumonia_score = calculate_score(pneumonia_kws)

    scores = {
        "respiratory": respiratory_score,
        "cardio": cardio_score,
        "gastro": gastro_score,
        "neuro": neuro_score
    }

    # Lấy phân hệ có điểm số triệu chứng cao nhất
    max_category = max(scores, key=scores.get)
    max_score = scores[max_category]

    # Nếu không phát hiện triệu chứng dương tính nào hoặc điểm số quá thấp (< 0.2)
    if max_score <= 0.2:
        return PredictResponse(
            predicted_disease="Triệu chứng không xác định",
            confidence_score=0.50,
            ai_recommendations=[
                "Thông tin triệu chứng cung cấp chưa đủ để phân loại cụ thể.",
                "Hãy uống nhiều nước ấm, ăn uống đủ chất dinh dưỡng và nghỉ ngơi hợp lý.",
                "Nên theo dõi sát sao nhiệt độ và các phản ứng phụ của cơ thể.",
                "Khuyến nghị đặt lịch khám trực tiếp với bác sĩ để có chẩn đoán chính xác nhất."
            ]
        )

    # 3. Phân nhánh chẩn đoán và trả về Lộ trình khuyến nghị điều trị lâm sàng
    if max_category == "respiratory":
        # Nếu có triệu chứng hô hấp kèm theo dấu hiệu nguy hiểm (khó thở, đau ngực)
        if pneumonia_score > 0.5:
            return PredictResponse(
                predicted_disease="Viêm đường hô hấp cấp / Viêm phổi",
                confidence_score=min(0.95, 0.70 + 0.05 * (respiratory_score + pneumonia_score)),
                ai_recommendations=[
                    "Đến cơ sở y tế gần nhất ngay lập tức để bác sĩ nghe phổi và chụp X-quang.",
                    "Tuyệt đối hạn chế vận động mạnh, ngồi nghỉ ngơi ở nơi thông thoáng.",
                    "Uống nhiều nước ấm và liên tục theo dõi chỉ số nồng độ oxy trong máu SpO2.",
                    "Chủ động đeo khẩu trang y tế để tránh nguy cơ lây nhiễm cho người thân xung quanh."
                ]
            )
        else:
            return PredictResponse(
                predicted_disease="Cúm mùa (Influenza)",
                confidence_score=min(0.95, 0.75 + 0.05 * respiratory_score),
                ai_recommendations=[
                    "Nghỉ ngơi tĩnh dưỡng tại giường, giữ ấm cơ thể và hạn chế tiếp xúc lạnh.",
                    "Uống nhiều nước ấm và bổ sung thêm nước hoa quả chứa nhiều Vitamin C.",
                    "Có thể sử dụng thuốc hạ sốt thông thường (Paracetamol) nếu thân nhiệt vượt quá 38.5°C.",
                    "Theo dõi triệu chứng, nếu sốt cao liên tục trên 3 ngày không hạ cần đến cơ sở y tế."
                ]
            )

    elif max_category == "cardio":
        return PredictResponse(
            predicted_disease="Nghi vấn bệnh lý Tim mạch / Đau thắt ngực",
            confidence_score=min(0.95, 0.70 + 0.05 * cardio_score),
            ai_recommendations=[
                "Dừng ngay mọi hoạt động thể lực, ngồi hoặc nằm nghỉ ngơi tĩnh lặng ở nơi thoáng mát.",
                "Tránh căng thẳng tâm lý tối đa, cố gắng hít thở sâu, chậm và đều đặn.",
                "Đặt lịch hẹn sớm với bác sĩ chuyên khoa Tim mạch để làm điện tâm đồ (ECG) và siêu âm tim.",
                "CẢNH BÁO: Nếu cơn đau thắt ngực dữ dội kéo dài quá 15 phút lan ra hàm hoặc cánh tay trái, hãy gọi cấp cứu 115 ngay lập tức."
            ]
        )

    elif max_category == "gastro":
        return PredictResponse(
            predicted_disease="Rối loạn tiêu hóa / Viêm dạ dày",
            confidence_score=min(0.95, 0.70 + 0.05 * gastro_score),
            ai_recommendations=[
                "Ăn các loại thức ăn lỏng, mềm, dễ tiêu hóa như cháo loãng. Tránh đồ ăn chua cay, dầu mỡ.",
                "Bổ sung nước và các chất điện giải (Oresol) pha đúng liều lượng nếu có tiêu chảy hoặc nôn.",
                "Sử dụng thêm các men vi sinh (probiotics) để hỗ trợ khôi phục hệ vi sinh đường ruột.",
                "LƯU Ý: Nếu đau bụng dữ dội tập trung vùng bụng dưới bên phải kèm sốt cao, cần đi viện ngay đề phòng viêm ruột thừa cấp."
            ]
        )

    elif max_category == "neuro":
        # Kiểm tra sâu xem triệu chứng thiên về suy nhược/stress hay chỉ đau đầu cơ học
        insomnia_stress_score = calculate_score(["mất ngủ", "insomnia", "lo âu", "căng thẳng", "stress"])
        if insomnia_stress_score > 0.5:
            return PredictResponse(
                predicted_disease="Hội chứng suy nhược thần kinh / Rối loạn lo âu",
                confidence_score=min(0.95, 0.65 + 0.05 * neuro_score),
                ai_recommendations=[
                    "Cải thiện tối đa vệ sinh giấc ngủ: tránh xa các thiết bị điện tử tối thiểu 1 giờ trước khi ngủ.",
                    "Thực hành thiền định, tập yoga nhẹ nhàng hoặc áp dụng các bài tập thở sâu 4-7-8.",
                    "Hạn chế sử dụng các chất kích thích thần kinh như cà phê, trà đặc vào buổi chiều/tối.",
                    "Nếu tình trạng mất ngủ kéo dài gây ảnh hưởng lớn đến cuộc sống, hãy tham vấn ý kiến bác sĩ tâm lý."
                ]
            )
        else:
            return PredictResponse(
                predicted_disease="Đau đầu do căng thẳng (Tension Headache)",
                confidence_score=min(0.95, 0.70 + 0.05 * neuro_score),
                ai_recommendations=[
                    "Nghỉ ngơi yên tĩnh trong phòng tối và thoáng khí.",
                    "Massage nhẹ nhàng vùng thái dương, trán và vùng cơ vai gáy bị co cứng.",
                    "Uống đủ nước trong ngày và tránh ngồi nhìn màn hình máy tính làm việc quá lâu.",
                    "Có thể cân nhắc dùng thuốc giảm đau cơ bản (Paracetamol) khi quá đau nhưng tuyệt đối không lạm dụng."
                ]
            )


@app.post("/predict-disease", response_model=PredictResponse)
@app.post("/ai/predict-disease", response_model=PredictResponse)
def predict_disease(request: SymptomRequest):
    """
    API tiếp nhận triệu chứng của bệnh nhân, trả về dự đoán bệnh lý và lộ trình chăm sóc sức khỏe.
    """
    try:
        if not request.symptoms.strip():
            raise HTTPException(status_code=400, detail="Symptoms text must not be empty")
        return analyze_symptoms_and_predict(request.symptoms)
    except HTTPException:
        raise
    except Exception as exc:
        raise HTTPException(status_code=500, detail=f"Symptom analysis failed: {exc}")

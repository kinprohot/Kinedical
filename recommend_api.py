from datetime import datetime, timezone
from typing import List, Optional

import numpy as np
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

app = FastAPI(title="KINEDICAL Recommendation API")


class Item(BaseModel):
    item_id: str = Field(..., description="Unique identifier of the health article")
    content: str = Field(..., description="Text content of the health article")
    views: int = Field(0, description="Number of views")
    likes: int = Field(0, description="Number of likes")
    saves: int = Field(0, description="Number of saves/bookmarks")
    published_at: Optional[str] = Field(None, description="Publish date in ISO format")
    vector: List[float] = Field(default_factory=list, description="Embedding vector for the article")


class RecommendRequest(BaseModel):
    user_id: str = Field(..., description="ID of the requesting user")
    items: List[Item] = Field(..., description="List of health articles to score")
    user_vector: List[float] = Field(default_factory=list, description="Embedding vector representing user preferences")
    history_vectors: List[List[float]] = Field(default_factory=list, description="Embedding vectors of previously interacted items")


class RecommendItem(BaseModel):
    item_id: str
    score: float


class RecommendResponse(BaseModel):
    user_id: str
    recommendations: List[RecommendItem]


class SymptomRequest(BaseModel):
    symptoms: str = Field(..., description="Raw text describing the patient's symptoms")


class PredictResponse(BaseModel):
    predicted_disease: str = Field(..., description="Name of the predicted disease")
    confidence_score: float = Field(..., description="Confidence score from 0.0 to 1.0")
    ai_recommendations: List[str] = Field(..., description="Actionable AI recommendations")



def parse_publish_date(date_str: Optional[str]) -> datetime:
    if not date_str:
        return datetime.now(timezone.utc)

    try:
        return datetime.fromisoformat(date_str)
    except ValueError:
        try:
            return datetime.strptime(date_str, "%Y-%m-%dT%H:%M:%S%z")
        except ValueError:
            return datetime.now(timezone.utc)


def normalize(scores: np.ndarray) -> np.ndarray:
    if scores.size == 0:
        return np.array([], dtype=float)
    min_val = float(scores.min())
    max_val = float(scores.max())
    if max_val <= min_val:
        return np.zeros_like(scores, dtype=float)
    return (scores - min_val) / (max_val - min_val)


def compute_popularity_scores(items: List[Item]) -> np.ndarray:
    views = np.array([item.views for item in items], dtype=float)
    likes = np.array([item.likes for item in items], dtype=float)
    saves = np.array([item.saves for item in items], dtype=float)

    now = datetime.now(timezone.utc)
    ages = np.array([
        (now - parse_publish_date(item.published_at)).days
        for item in items
    ], dtype=float)

    recency = 1.0 / (1.0 + ages)

    views_norm = normalize(np.log1p(views))
    likes_norm = normalize(np.log1p(likes))
    saves_norm = normalize(np.log1p(saves))
    recency_norm = normalize(recency)

    weights = np.array([0.40, 0.30, 0.20, 0.10], dtype=float)
    combined = np.vstack([views_norm, likes_norm, saves_norm, recency_norm]).T
    popularity_scores = combined.dot(weights)
    return popularity_scores


def compute_content_scores(request: RecommendRequest) -> np.ndarray:
    if request.user_vector and any(item.vector for item in request.items):
        item_vectors = np.array([
            item.vector if item.vector else [0.0] * len(request.user_vector)
            for item in request.items
        ], dtype=float)
        user_vec = np.array(request.user_vector, dtype=float).reshape(1, -1)
        return cosine_similarity(item_vectors, user_vec).flatten()

    documents = [item.content or "" for item in request.items]
    vectorizer = TfidfVectorizer(stop_words="english")
    tfidf = vectorizer.fit_transform(documents)
    centroid = tfidf.mean(axis=0)
    return cosine_similarity(tfidf, centroid).flatten()


def compute_cf_scores(request: RecommendRequest) -> np.ndarray:
    if not request.history_vectors or not any(request.history_vectors):
        return np.zeros(len(request.items), dtype=float)

    item_vectors = np.array([item.vector for item in request.items], dtype=float)
    history_matrix = np.array(request.history_vectors, dtype=float)
    if item_vectors.size == 0 or history_matrix.size == 0:
        return np.zeros(len(request.items), dtype=float)

    cf_matrix = cosine_similarity(item_vectors, history_matrix)
    return cf_matrix.mean(axis=1)


def compute_hybrid_scores(request: RecommendRequest, alpha: float = 0.7) -> np.ndarray:
    alpha = float(np.clip(alpha, 0.0, 1.0))
    content_scores = compute_content_scores(request)
    cf_scores = compute_cf_scores(request)
    content_norm = normalize(content_scores)
    cf_norm = normalize(cf_scores)
    return alpha * content_norm + (1.0 - alpha) * cf_norm


@app.post("/recommend", response_model=RecommendResponse)
def recommend(request: RecommendRequest, alpha: float = 0.7):
    try:
        if not request.items:
            raise HTTPException(status_code=400, detail="items must not be empty")

        if not request.user_vector:
            scores = compute_popularity_scores(request.items)
        else:
            scores = compute_hybrid_scores(request, alpha=alpha)

        scored_items = [
            RecommendItem(item_id=item.item_id, score=float(score))
            for item, score in zip(request.items, scores)
        ]

        top_items = sorted(scored_items, key=lambda x: x.score, reverse=True)[:5]
        return RecommendResponse(user_id=request.user_id, recommendations=top_items)

    except HTTPException:
        raise
    except Exception as exc:
        raise HTTPException(status_code=500, detail=f"Recommendation processing failed: {exc}")


def analyze_symptoms_and_predict(symptoms_text: str) -> PredictResponse:
    text = symptoms_text.lower()
    
    # Define clinical rules/mappings
    # Influenza / Respiratory Infection
    if any(k in text for k in ["sốt", "fever", "ho", "cough", "sổ mũi", "runny nose", "đau họng", "sore throat"]):
        if any(k in text for k in ["khó thở", "short of breath", "đau ngực", "chest pain"]):
            return PredictResponse(
                predicted_disease="Viêm đường hô hấp cấp / Viêm phổi",
                confidence_score=0.82,
                ai_recommendations=[
                    "Đến cơ sở y tế gần nhất ngay để kiểm tra phổi.",
                    "Hạn chế vận động mạnh và giữ ấm cơ thể.",
                    "Uống nhiều nước ấm và theo dõi nồng độ oxy SpO2 nếu có thể.",
                    "Đeo khẩu trang để tránh lây nhiễm cho người xung quanh."
                ]
            )
        return PredictResponse(
            predicted_disease="Cúm mùa (Influenza)",
            confidence_score=0.88,
            ai_recommendations=[
                "Nghỉ ngơi tại giường, giữ ấm cơ thể.",
                "Uống nhiều nước ấm và bổ sung Vitamin C.",
                "Sử dụng thuốc hạ sốt (Paracetamol) nếu sốt trên 38.5°C (tham khảo ý kiến dược sĩ/bác sĩ).",
                "Theo dõi các triệu chứng nếu kéo dài quá 3 ngày hoặc sốt cao không hạ."
            ]
        )
    
    # Cardiovascular
    if any(k in text for k in ["đau ngực", "chest pain", "nhói ngực", "khó thở", "hụt hơi", "tim đập nhanh", "palpitation"]):
        return PredictResponse(
            predicted_disease="Nghi vấn bệnh lý Tim mạch / Đau thắt ngực",
            confidence_score=0.78,
            ai_recommendations=[
                "Hạn chế hoạt động thể lực ngay lập tức, ngồi nghỉ ngơi nơi thoáng mát.",
                "Tránh căng thẳng tâm lý, cố gắng hít thở sâu và đều đặn.",
                "Cần đặt lịch hẹn sớm với bác sĩ chuyên khoa Tim mạch để đo điện tâm đồ (ECG).",
                "NẾU đau ngực dữ dội lan ra cánh tay hoặc hàm kéo dài quá 15 phút, hãy gọi cấp cứu ngay lập tức."
            ]
        )
    
    # Gastrointestinal
    if any(k in text for k in ["đau bụng", "stomachache", "đau dạ dày", "tiêu chảy", "diarrhea", "buồn nôn", "nôn", "nausea", "vomit", "đầy hơi"]):
        return PredictResponse(
            predicted_disease="Rối loạn tiêu hóa / Viêm dạ dày",
            confidence_score=0.85,
            ai_recommendations=[
                "Ăn cháo loãng hoặc các thức ăn mềm, dễ tiêu hóa. Tránh đồ cay nóng, dầu mỡ.",
                "Bổ sung nước và điện giải (Oresol) nếu bị tiêu chảy hoặc nôn mửa.",
                "Sử dụng men vi sinh hỗ trợ tiêu hóa.",
                "Nếu đau bụng dữ dội ở vùng hố chậu phải hoặc sốt cao kèm theo, cần đi khám ngay để loại trừ viêm ruột thừa."
            ]
        )

    # Neurological / Tension Headache / Stress
    if any(k in text for k in ["đau đầu", "headache", "chóng mặt", "dizzy", "mất ngủ", "insomnia", "stress", "căng thẳng", "lo âu"]):
        if any(k in text for k in ["mất ngủ", "insomnia", "lo âu", "căng thẳng", "stress"]):
            return PredictResponse(
                predicted_disease="Hội chứng suy nhược thần kinh / Rối loạn lo âu",
                confidence_score=0.75,
                ai_recommendations=[
                    "Cải thiện vệ sinh giấc ngủ: tắt thiết bị điện tử trước khi ngủ 1 giờ.",
                    "Thực hành thiền định, yoga hoặc các bài tập hít thở thư giãn cơ thể.",
                    "Giảm tiêu thụ caffeine (cà phê, trà đặc) và rượu bia.",
                    "Nếu tình trạng kéo dài gây ảnh hưởng lớn đến cuộc sống, hãy tham vấn ý kiến bác sĩ tâm lý."
                ]
            )
        return PredictResponse(
            predicted_disease="Đau đầu do căng thẳng (Tension Headache)",
            confidence_score=0.80,
            ai_recommendations=[
                "Nghỉ ngơi trong phòng tối và yên tĩnh.",
                "Massage nhẹ nhàng vùng thái dương và cổ vai gáy.",
                "Uống đủ nước và tránh nhìn màn hình điện tử liên tục.",
                "Có thể dùng thuốc giảm đau thông thường (Paracetamol) nhưng không được lạm dụng."
            ]
        )

    # General/Default
    return PredictResponse(
        predicted_disease="Triệu chứng không xác định",
        confidence_score=0.50,
        ai_recommendations=[
            "Thông tin triệu chứng cung cấp chưa đủ để phân loại cụ thể.",
            "Hãy uống nhiều nước, ăn uống đủ chất và nghỉ ngơi hợp lý.",
            "Nên theo dõi sát sao tình trạng sức khỏe của bản thân.",
            "Khuyến nghị đặt lịch khám trực tiếp với bác sĩ để có chẩn đoán chính xác nhất."
        ]
    )


@app.post("/predict-disease", response_model=PredictResponse)
@app.post("/ai/predict-disease", response_model=PredictResponse)
def predict_disease(request: SymptomRequest):
    try:
        if not request.symptoms.strip():
            raise HTTPException(status_code=400, detail="Symptoms text must not be empty")
        return analyze_symptoms_and_predict(request.symptoms)
    except HTTPException:
        raise
    except Exception as exc:
        raise HTTPException(status_code=500, detail=f"Symptom analysis failed: {exc}")


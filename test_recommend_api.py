from datetime import datetime, timezone
import pytest
from fastapi.testclient import TestClient

from recommend_api import (
    app,
    parse_publish_date,
    compute_popularity_scores,
    compute_cf_scores,
    analyze_symptoms_and_predict,
    Item,
    RecommendRequest
)

client = TestClient(app)


# --- EXISTING TESTS (PRESERVED & ENHANCED) ---

def test_recommend_endpoint_returns_200_and_top_five():
    payload = {
        "user_id": "user-001",
        "items": [
            {"item_id": "item1", "content": "Heart health article", "views": 10, "likes": 5, "saves": 1, "published_at": "2026-01-10T08:00:00Z"},
            {"item_id": "item2", "content": "Nutrition tips for adults", "views": 100, "likes": 20, "saves": 10, "published_at": "2026-04-01T08:00:00Z"},
            {"item_id": "item3", "content": "Mental wellness and stress management", "views": 50, "likes": 15, "saves": 7, "published_at": "2026-03-01T08:00:00Z"},
            {"item_id": "item4", "content": "Exercise routine for beginners", "views": 30, "likes": 10, "saves": 5, "published_at": "2025-12-01T08:00:00Z"},
            {"item_id": "item5", "content": "Sleep hygiene and recovery", "views": 70, "likes": 25, "saves": 12, "published_at": "2026-02-15T08:00:00Z"},
            {"item_id": "item6", "content": "Healthy eating for chronic disease", "views": 20, "likes": 4, "saves": 2, "published_at": "2026-04-15T08:00:00Z"}
        ]
    }

    response = client.post("/recommend", json=payload)
    assert response.status_code == 200

    json_data = response.json()
    assert json_data["user_id"] == "user-001"
    assert "recommendations" in json_data
    assert len(json_data["recommendations"]) == 5

    scores = [item["score"] for item in json_data["recommendations"]]
    assert all(scores[i] >= scores[i + 1] for i in range(len(scores) - 1))
    assert json_data["recommendations"][0]["item_id"] == "item2"


def test_predict_disease_endpoint_flu():
    payload = {"symptoms": "Tôi bị sốt cao kèm theo ho và đau họng"}
    response = client.post("/ai/predict-disease", json=payload)
    assert response.status_code == 200
    
    json_data = response.json()
    assert json_data["predicted_disease"] == "Cúm mùa (Influenza)"
    assert json_data["confidence_score"] >= 0.75
    assert len(json_data["ai_recommendations"]) > 0


def test_predict_disease_endpoint_cardio():
    payload = {"symptoms": "Đau ngực dữ dội và khó thở khi vận động"}
    response = client.post("/predict-disease", json=payload)
    assert response.status_code == 200
    
    json_data = response.json()
    assert json_data["predicted_disease"] == "Nghi vấn bệnh lý Tim mạch / Đau thắt ngực"
    assert json_data["confidence_score"] >= 0.70


def test_predict_disease_endpoint_empty():
    payload = {"symptoms": ""}
    response = client.post("/predict-disease", json=payload)
    assert response.status_code == 400


# --- NEW COMPREHENSIVE EDGE-CASE TESTS ---

def test_parse_publish_date_timezone_safety():
    """
    Xác minh hàm parse_publish_date biến đổi chuỗi naive ISO thành dạng aware UTC thành công.
    Giúp ngăn lỗi: TypeError: can't subtract offset-naive and offset-aware datetimes
    """
    naive_date_str = "2026-05-24T02:00:00"
    dt = parse_publish_date(naive_date_str)
    
    assert dt.tzinfo is not None, "Kết quả trả về phải là một Aware Datetime!"
    assert dt.tzinfo == timezone.utc, "Múi giờ mặc định gán cho naive datetime phải là UTC!"


def test_compute_popularity_future_dates_safety():
    """
    Xác minh tính ổn định của hàm compute_popularity_scores khi xử lý bài viết có ngày đăng tương lai.
    Tránh lỗi ZeroDivisionError do divisor = 1.0 + ages = 0.0, hoặc recency bị âm.
    """
    items = [
        Item(item_id="item1", content="Cardio Advice", views=100, likes=20, saves=5, published_at="2027-05-24T00:00:00Z"), # Tương lai
        Item(item_id="item2", content="General Nutrition", views=50, likes=10, saves=2, published_at="2025-05-24T00:00:00Z") # Quá khứ
    ]
    
    # Tính điểm
    scores = compute_popularity_scores(items)
    
    # Xác nhận tính toán thành công mà không gây lỗi NaN hay vô cực (inf)
    assert len(scores) == 2
    import math
    assert not any(math.isnan(s) or math.isinf(s) for s in scores)
    assert all(s >= 0.0 for s in scores)


def test_compute_cf_scores_missing_vectors_safety():
    """
    Xác minh độ an toàn của thuật toán Collaborative Filtering (CF) khi bài viết thiếu vector nhúng.
    Đảm bảo mảng rỗng được padding tự động mà không gây crash ValueError.
    """
    request = RecommendRequest(
        user_id="user-001",
        user_vector=[],
        history_vectors=[[0.5, 0.5, 0.5]], # dim = 3
        items=[
            Item(item_id="item1", content="Nội dung 1", vector=[0.5, 0.5, 0.5]),
            Item(item_id="item2", content="Nội dung 2", vector=[]),  # Vector nhúng trống!
            Item(item_id="item3", content="Nội dung 3", vector=[0.1, 0.1])  # Sai chiều (dim=2)
        ]
    )
    
    # Tính điểm CF
    cf_scores = compute_cf_scores(request)
    
    # Xác nhận tính toán thành công
    assert len(cf_scores) == 3
    assert cf_scores[0] > cf_scores[1]
    assert cf_scores[0] > cf_scores[2]


def test_symptom_checker_negation_processing():
    """
    Xác minh xử lý phủ định trong chẩn đoán AI hoạt động chính xác.
    Đảm bảo rằng 'không bị sốt' không kích hoạt dự đoán 'Cúm mùa'.
    """
    # Triệu chứng chứa từ "không bị sốt", nhưng khẳng định đau ngực tim nhói
    input_text = "Tôi hoàn toàn không bị sốt và không ho gì cả, tôi chỉ bị nhói ngực dữ dội kèm hụt hơi thôi"
    
    res = analyze_symptoms_and_predict(input_text)
    
    # Kết quả dự đoán phải hướng tới bệnh Tim mạch thay vì Cúm mùa
    assert res.predicted_disease == "Nghi vấn bệnh lý Tim mạch / Đau thắt ngực"
    assert res.confidence_score >= 0.70

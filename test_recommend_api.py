from fastapi.testclient import TestClient

from recommend_api import app

client = TestClient(app)


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
    assert json_data["confidence_score"] == 0.88
    assert len(json_data["ai_recommendations"]) > 0


def test_predict_disease_endpoint_cardio():
    payload = {"symptoms": "Đau ngực dữ dội và khó thở khi vận động"}
    response = client.post("/predict-disease", json=payload)
    assert response.status_code == 200
    
    json_data = response.json()
    assert json_data["predicted_disease"] == "Nghi vấn bệnh lý Tim mạch / Đau thắt ngực"
    assert json_data["confidence_score"] == 0.78


def test_predict_disease_endpoint_empty():
    payload = {"symptoms": ""}
    response = client.post("/predict-disease", json=payload)
    assert response.status_code == 400


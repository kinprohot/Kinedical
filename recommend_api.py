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

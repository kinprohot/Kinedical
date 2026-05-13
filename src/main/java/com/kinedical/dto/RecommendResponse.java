package com.kinedical.dto;

import java.util.List;

public class RecommendResponse {

    private String userId;
    private List<RecommendItem> recommendations;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<RecommendItem> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<RecommendItem> recommendations) {
        this.recommendations = recommendations;
    }

    public static class RecommendItem {
        private String itemId;
        private double score;

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }
    }
}

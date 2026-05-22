package com.kinedical.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecommendRequest {

    @JsonProperty("user_id")
    private String userId;

    private List<Item> items;

    @JsonProperty("user_vector")
    private List<Double> userVector;

    @JsonProperty("history_vectors")
    private List<List<Double>> historyVectors;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<Double> getUserVector() {
        return userVector;
    }

    public void setUserVector(List<Double> userVector) {
        this.userVector = userVector;
    }

    public List<List<Double>> getHistoryVectors() {
        return historyVectors;
    }

    public void setHistoryVectors(List<List<Double>> historyVectors) {
        this.historyVectors = historyVectors;
    }

    public static class Item {
        @JsonProperty("item_id")
        private String itemId;

        private String content;

        private int views;
        private int likes;
        private int saves;

        @JsonProperty("published_at")
        private String publishedAt;

        private List<Double> vector;

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getViews() {
            return views;
        }

        public void setViews(int views) {
            this.views = views;
        }

        public int getLikes() {
            return likes;
        }

        public void setLikes(int likes) {
            this.likes = likes;
        }

        public int getSaves() {
            return saves;
        }

        public void setSaves(int saves) {
            this.saves = saves;
        }

        public String getPublishedAt() {
            return publishedAt;
        }

        public void setPublishedAt(String publishedAt) {
            this.publishedAt = publishedAt;
        }

        public List<Double> getVector() {
            return vector;
        }

        public void setVector(List<Double> vector) {
            this.vector = vector;
        }
    }
}

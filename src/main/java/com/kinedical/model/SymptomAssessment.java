package com.kinedical.model;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "symptom_assessments")
public class SymptomAssessment {

    @Id
    private String id;

    @Indexed
    private String patientId;

    private String reportedSymptoms;
    private String predictedDiseases;
    private Double confidenceScore;
    private List<String> aiRecommendations;
    private Instant createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getReportedSymptoms() {
        return reportedSymptoms;
    }

    public void setReportedSymptoms(String reportedSymptoms) {
        this.reportedSymptoms = reportedSymptoms;
    }

    public String getPredictedDiseases() {
        return predictedDiseases;
    }

    public void setPredictedDiseases(String predictedDiseases) {
        this.predictedDiseases = predictedDiseases;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public List<String> getAiRecommendations() {
        return aiRecommendations;
    }

    public void setAiRecommendations(List<String> aiRecommendations) {
        this.aiRecommendations = aiRecommendations;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}

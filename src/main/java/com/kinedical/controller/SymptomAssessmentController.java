package com.kinedical.controller;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kinedical.model.SymptomAssessment;
import com.kinedical.repository.SymptomAssessmentRepository;
import com.kinedical.security.AppUserDetails;

@RestController
@RequestMapping("/api/symptoms")
public class SymptomAssessmentController {

    private final SymptomAssessmentRepository symptomAssessmentRepository;
    private final WebClient recommendWebClient;

    public SymptomAssessmentController(SymptomAssessmentRepository symptomAssessmentRepository, WebClient recommendWebClient) {
        this.symptomAssessmentRepository = symptomAssessmentRepository;
        this.recommendWebClient = recommendWebClient;
    }

    @GetMapping
    public ResponseEntity<List<SymptomAssessment>> getHistory(Authentication authentication) {
        String patientId = resolveUserId(authentication);
        if (patientId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<SymptomAssessment> history = symptomAssessmentRepository.findByPatientIdOrderByCreatedAtDesc(patientId);
        return ResponseEntity.ok(history);
    }

    @PostMapping("/analyze")
    public ResponseEntity<SymptomAssessment> analyzeSymptoms(@RequestBody AnalyzeRequest request, Authentication authentication) {
        String patientId = resolveUserId(authentication);
        if (patientId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (request.getSymptoms() == null || request.getSymptoms().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            // Call FastAPI predict-disease endpoint
            FastApiPredictResponse predictResponse = recommendWebClient.post()
                    .uri("/ai/predict-disease")
                    .bodyValue(new FastApiPredictRequest(request.getSymptoms()))
                    .retrieve()
                    .bodyToMono(FastApiPredictResponse.class)
                    .block(); // Block synchronously for the REST controller

            if (predictResponse == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            // Save to database
            SymptomAssessment assessment = new SymptomAssessment();
            assessment.setPatientId(patientId);
            assessment.setReportedSymptoms(request.getSymptoms());
            assessment.setPredictedDiseases(predictResponse.getPredictedDisease());
            assessment.setConfidenceScore(predictResponse.getConfidenceScore());
            assessment.setAiRecommendations(predictResponse.getAiRecommendations());
            assessment.setCreatedAt(Instant.now());

            SymptomAssessment saved = symptomAssessmentRepository.save(assessment);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String resolveUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof AppUserDetails details) {
            return details.getUserId();
        }
        return null;
    }

    // Helper classes for requests and responses
    public static class AnalyzeRequest {
        private String symptoms;

        public String getSymptoms() {
            return symptoms;
        }

        public void setSymptoms(String symptoms) {
            this.symptoms = symptoms;
        }
    }

    public static class FastApiPredictRequest {
        private String symptoms;

        public FastApiPredictRequest(String symptoms) {
            this.symptoms = symptoms;
        }

        public String getSymptoms() {
            return symptoms;
        }

        public void setSymptoms(String symptoms) {
            this.symptoms = symptoms;
        }
    }

    public static class FastApiPredictResponse {
        @JsonProperty("predicted_disease")
        private String predictedDisease;

        @JsonProperty("confidence_score")
        private Double confidenceScore;

        @JsonProperty("ai_recommendations")
        private List<String> aiRecommendations;

        public String getPredictedDisease() {
            return predictedDisease;
        }

        public void setPredictedDisease(String predictedDisease) {
            this.predictedDisease = predictedDisease;
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
    }
}

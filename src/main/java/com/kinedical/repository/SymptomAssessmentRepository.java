package com.kinedical.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.kinedical.model.SymptomAssessment;

public interface SymptomAssessmentRepository extends MongoRepository<SymptomAssessment, String> {
    List<SymptomAssessment> findByPatientIdOrderByCreatedAtDesc(String patientId);
}

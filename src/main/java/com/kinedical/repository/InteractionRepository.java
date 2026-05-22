package com.kinedical.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.kinedical.model.Interaction;

public interface InteractionRepository extends MongoRepository<Interaction, String> {
    List<Interaction> findByPatientId(String patientId);
    List<Interaction> findByPatientIdAndContentId(String patientId, String contentId);
}

package com.kinedical.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.kinedical.model.MedicalRecord;

public interface MedicalRecordRepository extends MongoRepository<MedicalRecord, String> {
    List<MedicalRecord> findByPatientIdOrderByVisitDateDesc(String patientId);

    List<MedicalRecord> findByDoctorIdOrderByVisitDateDesc(String doctorId);
}

package com.kinedical.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kinedical.model.MedicalRecord;
import com.kinedical.repository.MedicalRecordRepository;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;

    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
    }

    public MedicalRecord create(MedicalRecord medicalRecord) {
        Instant now = Instant.now();
        medicalRecord.setCreatedAt(now);
        medicalRecord.setUpdatedAt(now);
        return medicalRecordRepository.save(medicalRecord);
    }

    public List<MedicalRecord> findAll() {
        return medicalRecordRepository.findAll();
    }

    public Optional<MedicalRecord> findById(String id) {
        return medicalRecordRepository.findById(id);
    }

    public MedicalRecord update(String id, MedicalRecord updatedRecord) {
        return medicalRecordRepository.findById(id)
                .map(existing -> {
                    existing.setPatientId(updatedRecord.getPatientId());
                    existing.setDoctorId(updatedRecord.getDoctorId());
                    existing.setVisitDate(updatedRecord.getVisitDate());
                    existing.setRecordType(updatedRecord.getRecordType());
                    existing.setStatus(updatedRecord.getStatus());
                    existing.setSummary(updatedRecord.getSummary());
                    existing.setDiagnoses(updatedRecord.getDiagnoses());
                    existing.setSymptoms(updatedRecord.getSymptoms());
                    existing.setVitals(updatedRecord.getVitals());
                    existing.setLabResults(updatedRecord.getLabResults());
                    existing.setMedications(updatedRecord.getMedications());
                    existing.setAttachments(updatedRecord.getAttachments());
                    existing.setCustomFields(updatedRecord.getCustomFields());
                    existing.setNotes(updatedRecord.getNotes());
                    existing.setUpdatedBy(updatedRecord.getUpdatedBy());
                    existing.setUpdatedAt(Instant.now());
                    return medicalRecordRepository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("MedicalRecord not found: " + id));
    }

    public void delete(String id) {
        medicalRecordRepository.deleteById(id);
    }
}

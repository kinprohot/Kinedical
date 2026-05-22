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
    private final AuditLogService auditLogService;

    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository, AuditLogService auditLogService) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.auditLogService = auditLogService;
    }

    public MedicalRecord create(MedicalRecord medicalRecord, String createdBy) {
        Instant now = Instant.now();
        medicalRecord.setCreatedAt(now);
        medicalRecord.setUpdatedAt(now);
        medicalRecord.setUpdatedBy(createdBy);
        MedicalRecord saved = medicalRecordRepository.save(medicalRecord);
        auditLogService.log("CREATE_RECORD", createdBy, "MedicalRecord", saved.getId(), "Created medical record.");
        return saved;
    }

    public List<MedicalRecord> findAll() {
        return medicalRecordRepository.findAll();
    }

    public List<MedicalRecord> findAllForUser(String userId, String role) {
        if ("PATIENT".equalsIgnoreCase(role)) {
            return medicalRecordRepository.findByPatientIdOrderByVisitDateDesc(userId);
        }
        return findAll();
    }

    public Optional<MedicalRecord> findById(String id) {
        return medicalRecordRepository.findById(id);
    }

    public Optional<MedicalRecord> findByIdForUser(String id, String userId, String role) {
        return findById(id).filter(record -> {
            if ("PATIENT".equalsIgnoreCase(role)) {
                return record.getPatientId() != null && record.getPatientId().equals(userId);
            }
            return true;
        });
    }

    public MedicalRecord update(String id, MedicalRecord updatedRecord, String updatedBy) {
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
                    existing.setUpdatedBy(updatedBy);
                    existing.setUpdatedAt(Instant.now());
                    MedicalRecord saved = medicalRecordRepository.save(existing);
                    auditLogService.log("UPDATE_RECORD", updatedBy, "MedicalRecord", saved.getId(),
                            "Updated medical record.");
                    return saved;
                })
                .orElseThrow(() -> new IllegalArgumentException("MedicalRecord not found: " + id));
    }

    public void delete(String id, String deletedBy) {
        medicalRecordRepository.deleteById(id);
        auditLogService.log("DELETE_RECORD", deletedBy, "MedicalRecord", id, "Deleted medical record.");
    }
}

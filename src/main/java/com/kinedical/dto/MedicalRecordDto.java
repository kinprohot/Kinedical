package com.kinedical.dto;

import java.time.Instant;
import java.util.List;

import com.kinedical.model.MedicalRecord;

public class MedicalRecordDto {

    private String id;
    private String patientId;
    private String doctorId;
    private Instant visitDate;
    private MedicalRecord.RecordType recordType;
    private MedicalRecord.Status status;
    private String summary;
    private List<MedicalRecord.Diagnosis> diagnoses;
    private List<String> symptoms;
    private MedicalRecord.VitalSigns vitals;
    private List<MedicalRecord.LabResult> labResults;
    private List<MedicalRecord.Medication> medications;
    private List<MedicalRecord.Attachment> attachments;
    private Object customFields;
    private String notes;
    private String updatedBy;

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

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public Instant getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Instant visitDate) {
        this.visitDate = visitDate;
    }

    public MedicalRecord.RecordType getRecordType() {
        return recordType;
    }

    public void setRecordType(MedicalRecord.RecordType recordType) {
        this.recordType = recordType;
    }

    public MedicalRecord.Status getStatus() {
        return status;
    }

    public void setStatus(MedicalRecord.Status status) {
        this.status = status;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<MedicalRecord.Diagnosis> getDiagnoses() {
        return diagnoses;
    }

    public void setDiagnoses(List<MedicalRecord.Diagnosis> diagnoses) {
        this.diagnoses = diagnoses;
    }

    public List<String> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(List<String> symptoms) {
        this.symptoms = symptoms;
    }

    public MedicalRecord.VitalSigns getVitals() {
        return vitals;
    }

    public void setVitals(MedicalRecord.VitalSigns vitals) {
        this.vitals = vitals;
    }

    public List<MedicalRecord.LabResult> getLabResults() {
        return labResults;
    }

    public void setLabResults(List<MedicalRecord.LabResult> labResults) {
        this.labResults = labResults;
    }

    public List<MedicalRecord.Medication> getMedications() {
        return medications;
    }

    public void setMedications(List<MedicalRecord.Medication> medications) {
        this.medications = medications;
    }

    public List<MedicalRecord.Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<MedicalRecord.Attachment> attachments) {
        this.attachments = attachments;
    }

    public Object getCustomFields() {
        return customFields;
    }

    public void setCustomFields(Object customFields) {
        this.customFields = customFields;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public MedicalRecord toEntity() {
        MedicalRecord entity = new MedicalRecord();
        entity.setId(id);
        entity.setPatientId(patientId);
        entity.setDoctorId(doctorId);
        entity.setVisitDate(visitDate);
        entity.setRecordType(recordType);
        entity.setStatus(status);
        entity.setSummary(summary);
        entity.setDiagnoses(diagnoses);
        entity.setSymptoms(symptoms);
        entity.setVitals(vitals);
        entity.setLabResults(labResults);
        entity.setMedications(medications);
        entity.setAttachments(attachments);
        entity.setCustomFields(customFields);
        entity.setNotes(notes);
        entity.setUpdatedBy(updatedBy);
        return entity;
    }

    public static MedicalRecordDto fromEntity(MedicalRecord entity) {
        MedicalRecordDto dto = new MedicalRecordDto();
        dto.setId(entity.getId());
        dto.setPatientId(entity.getPatientId());
        dto.setDoctorId(entity.getDoctorId());
        dto.setVisitDate(entity.getVisitDate());
        dto.setRecordType(entity.getRecordType());
        dto.setStatus(entity.getStatus());
        dto.setSummary(entity.getSummary());
        dto.setDiagnoses(entity.getDiagnoses());
        dto.setSymptoms(entity.getSymptoms());
        dto.setVitals(entity.getVitals());
        dto.setLabResults(entity.getLabResults());
        dto.setMedications(entity.getMedications());
        dto.setAttachments(entity.getAttachments());
        dto.setCustomFields(entity.getCustomFields());
        dto.setNotes(entity.getNotes());
        dto.setUpdatedBy(entity.getUpdatedBy());
        return dto;
    }
}

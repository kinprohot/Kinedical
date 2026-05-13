package com.kinedical.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.Optional;

import com.kinedical.model.MedicalRecord;
import com.kinedical.service.MedicalRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MedicalRecordController.class)
class MedicalRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedicalRecordService medicalRecordService;

    @Test
    void shouldReturnMedicalRecordWhenExists() throws Exception {
        MedicalRecord record = new MedicalRecord();
        record.setId("rec-123");
        record.setPatientId("pat-456");
        record.setDoctorId("doc-789");
        record.setSummary("Test medical record summary.");
        record.setVisitDate(Instant.parse("2025-01-01T10:00:00Z"));

        when(medicalRecordService.findById("rec-123")).thenReturn(Optional.of(record));

        mockMvc.perform(get("/api/medical-records/rec-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("rec-123"))
                .andExpect(jsonPath("$.patientId").value("pat-456"))
                .andExpect(jsonPath("$.doctorId").value("doc-789"));
    }

    @Test
    void shouldReturnNotFoundWhenMedicalRecordMissing() throws Exception {
        when(medicalRecordService.findById(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/medical-records/missing-id"))
                .andExpect(status().isNotFound());
    }
}

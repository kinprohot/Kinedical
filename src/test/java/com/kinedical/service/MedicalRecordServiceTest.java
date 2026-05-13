package com.kinedical.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import com.kinedical.model.MedicalRecord;
import com.kinedical.repository.MedicalRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceTest {

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @InjectMocks
    private MedicalRecordService medicalRecordService;

    private MedicalRecord record;

    @BeforeEach
    void setUp() {
        record = new MedicalRecord();
        record.setId("rec-123");
        record.setPatientId("pat-456");
        record.setDoctorId("doc-789");
        record.setSummary("Test medical record summary.");
        record.setVisitDate(Instant.parse("2025-01-01T10:00:00Z"));
    }

    @Test
    void shouldFindMedicalRecordById() {
        when(medicalRecordRepository.findById("rec-123")).thenReturn(Optional.of(record));

        Optional<MedicalRecord> result = medicalRecordService.findById("rec-123");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("rec-123");
        assertThat(result.get().getPatientId()).isEqualTo("pat-456");
        verify(medicalRecordRepository).findById("rec-123");
    }

    @Test
    void shouldReturnEmptyWhenMedicalRecordNotFound() {
        when(medicalRecordRepository.findById(anyString())).thenReturn(Optional.empty());

        Optional<MedicalRecord> result = medicalRecordService.findById("missing-id");

        assertThat(result).isNotPresent();
        verify(medicalRecordRepository).findById("missing-id");
    }
}

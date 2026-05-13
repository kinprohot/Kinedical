package com.kinedical.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kinedical.dto.MedicalRecordDto;
import com.kinedical.model.MedicalRecord;
import com.kinedical.service.MedicalRecordService;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @GetMapping
    public List<MedicalRecordDto> getAll() {
        return medicalRecordService.findAll().stream()
                .map(MedicalRecordDto::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecordDto> getById(@PathVariable String id) {
        return medicalRecordService.findById(id)
                .map(MedicalRecordDto::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MedicalRecordDto> create(@RequestBody MedicalRecordDto request) {
        MedicalRecord saved = medicalRecordService.create(request.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).body(MedicalRecordDto.fromEntity(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalRecordDto> update(@PathVariable String id, @RequestBody MedicalRecordDto request) {
        try {
            MedicalRecord saved = medicalRecordService.update(id, request.toEntity());
            return ResponseEntity.ok(MedicalRecordDto.fromEntity(saved));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        medicalRecordService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

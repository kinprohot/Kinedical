package com.kinedical.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
import com.kinedical.security.AppUserDetails;
import com.kinedical.service.MedicalRecordService;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @GetMapping
    public List<MedicalRecordDto> getAll(Authentication authentication) {
        String userId = resolveUserId(authentication);
        String role = resolveUserRole(authentication);
        return medicalRecordService.findAllForUser(userId, role).stream()
                .map(MedicalRecordDto::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecordDto> getById(@PathVariable String id, Authentication authentication) {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication();
        var optional = medicalRecordService.findById(id);
        if (optional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        MedicalRecord record = optional.get();
        if (auth != null) {
            // allow doctors and admins
            boolean isDoctorOrAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR")
                            || a.getAuthority().equals("ROLE_ADMIN"));
            if (isDoctorOrAdmin) {
                return ResponseEntity.ok(MedicalRecordDto.fromEntity(record));
            }
            // allow owning patient
            if (auth.getPrincipal() instanceof AppUserDetails details) {
                String userId = details.getUserId();
                if (record.getPatientId() != null && record.getPatientId().equals(userId)) {
                    return ResponseEntity.ok(MedicalRecordDto.fromEntity(record));
                }
            }
        }
        return ResponseEntity.<MedicalRecordDto>status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public ResponseEntity<MedicalRecordDto> create(@RequestBody MedicalRecordDto request,
            Authentication authentication) {
        String createdBy = resolveUserId(authentication);
        MedicalRecord saved = medicalRecordService.create(request.toEntity(), createdBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(MedicalRecordDto.fromEntity(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public ResponseEntity<MedicalRecordDto> update(@PathVariable String id, @RequestBody MedicalRecordDto request,
            Authentication authentication) {
        try {
            String updatedBy = resolveUserId(authentication);
            MedicalRecord saved = medicalRecordService.update(id, request.toEntity(), updatedBy);
            return ResponseEntity.ok(MedicalRecordDto.fromEntity(saved));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id, Authentication authentication) {
        medicalRecordService.delete(id, resolveUserId(authentication));
        return ResponseEntity.noContent().build();
    }

    private String resolveUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof AppUserDetails details) {
            return details.getUserId();
        }
        return null;
    }

    private String resolveUserRole(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof AppUserDetails details) {
            return details.getRole().name();
        }
        return "PATIENT";
    }
}

package com.kinedical.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.kinedical.model.AuditLog;
import com.kinedical.repository.AuditLogRepository;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(String eventType, String userId, String resourceType, String resourceId, String details) {
        AuditLog audit = new AuditLog();
        audit.setEventType(eventType);
        audit.setUserId(userId);
        audit.setResourceType(resourceType);
        audit.setResourceId(resourceId);
        audit.setDetails(details);
        audit.setCreatedAt(Instant.now());
        auditLogRepository.save(audit);
    }
}

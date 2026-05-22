package com.kinedical.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.kinedical.model.AuditLog;

public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
}

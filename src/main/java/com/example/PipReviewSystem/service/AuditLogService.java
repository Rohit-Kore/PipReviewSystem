package com.example.PipReviewSystem.service;

import com.example.PipReviewSystem.entity.AuditLog;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface AuditLogService {
    ResponseEntity<?> addAuditLog(AuditLog log);
    ResponseEntity<?> getAllLogs();
    ResponseEntity<?> getLogsByUserId(UUID userId);
    ResponseEntity<?> deleteLog(UUID logId);
}
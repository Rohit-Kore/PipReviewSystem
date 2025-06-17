package com.example.PipReviewSystem.repository;

import com.example.PipReviewSystem.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByUserEmployeeId(Long userId);
    List<AuditLog> findByEntity(String entity);
    List<AuditLog> findByAction(String action);
}

package com.example.PipReviewSystem.controller;


import com.example.PipReviewSystem.entity.AuditLog;
import com.example.PipReviewSystem.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;


@RestController
@RequestMapping("/api/audit")
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'HR')")
    @PostMapping("/add")
    public ResponseEntity<?> addLog(@RequestBody AuditLog log) {
        return auditLogService.addAuditLog(log);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<?> getAllLogs() {
        return auditLogService.getAllLogs();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'HR')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getLogsByUser(@PathVariable UUID userId) {
        return auditLogService.getLogsByUserId(userId);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/delete/{logId}")
    public ResponseEntity<?> deleteLog(@PathVariable UUID logId) {
        return auditLogService.deleteLog(logId);
    }
}









//
//import com.example.PipReviewSystem.entity.AuditLog;
//import com.example.PipReviewSystem.service.AuditLogService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.UUID;
//
//
//@RestController
//@RequestMapping("/api/audit-logs")
//public class AuditLogController {
//
//    @Autowired
//    private AuditLogService auditLogService;
//
//    @PostMapping
//    public ResponseEntity<?> createLog(@RequestBody AuditLog auditLog) {
//        return auditLogService.createAuditLog(auditLog);
//    }
//
//    @GetMapping
//    public ResponseEntity<?> getAllLogs() {
//        return auditLogService.getAllLogs();
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<?> getLogById(@PathVariable UUID id) {
//        return auditLogService.getLogById(id);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<?> updateLog(@PathVariable UUID id, @RequestBody AuditLog updatedLog) {
//        return auditLogService.updateAuditLog(id, updatedLog);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deleteLog(@PathVariable UUID id) {
//        return auditLogService.deleteLog(id);
//    }
//
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<?> getLogsByUser(@PathVariable UUID userId) {
//        return auditLogService.getLogsByUserId(userId);
//    }
//}

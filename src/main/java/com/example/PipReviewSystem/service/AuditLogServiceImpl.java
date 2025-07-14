package com.example.PipReviewSystem.service;

import com.example.PipReviewSystem.entity.AuditLog;
import com.example.PipReviewSystem.entity.Employee;
import com.example.PipReviewSystem.repository.AuditLogRepository;
import com.example.PipReviewSystem.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public ResponseEntity<?> addAuditLog(AuditLog log) {
        Optional<Employee> employee = employeeRepository.findById(log.getUser().getEmployeeId());
        if (employee.isPresent()) {
            log.setUser(employee.get());
            log.setTimestamp(LocalDateTime.now());
            AuditLog saved = auditLogRepository.save(log);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<?> getAllLogs() {
        List<AuditLog> logs = auditLogRepository.findAll();
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getLogsByUserId(UUID userId) {
        List<AuditLog> logs = auditLogRepository.findByUser_EmployeeId(userId);
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> deleteLog(UUID logId) {
        Optional<AuditLog> log = auditLogRepository.findById(logId);
        if (log.isPresent()) {
            auditLogRepository.delete(log.get());
            return new ResponseEntity<>("Log deleted", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Log not found", HttpStatus.NOT_FOUND);
        }
    }
}






























//
//import com.example.PipReviewSystem.entity.AuditLog;
//import com.example.PipReviewSystem.entity.Employee;
//import com.example.PipReviewSystem.repository.AuditLogRepository;
//import com.example.PipReviewSystem.repository.EmployeeRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.UUID;
//
//@Service
//public class AuditLogService {
//
//    @Autowired
//    private AuditLogRepository auditLogRepository;
//
//    @Autowired
//    private EmployeeRepository employeeRepository;
//
//    public ResponseEntity<?> createAuditLog(AuditLog auditLog) {
//        if (auditLog.getUser() == null || auditLog.getUser().getEmployeeId() == null) {
//            return new ResponseEntity<>("User ID is required", HttpStatus.BAD_REQUEST);
//        }
//
//        Optional<Employee> employee = employeeRepository.findById(auditLog.getUser().getEmployeeId());
//        if (employee.isPresent()) {
//            auditLog.setUser(employee.get());
//            auditLog.setTimestamp(LocalDateTime.now());
//            AuditLog savedLog = auditLogRepository.save(auditLog);
//            return new ResponseEntity<>(savedLog, HttpStatus.CREATED);
//        } else {
//            return new ResponseEntity<>("Employee not found", HttpStatus.NOT_FOUND);
//        }
//    }
//
//    public ResponseEntity<?> getAllLogs() {
//        List<AuditLog> logs = auditLogRepository.findAll();
//        if (logs.isEmpty()) {
//            return new ResponseEntity<>("No audit logs found", HttpStatus.NOT_FOUND);
//        } else {
//            return new ResponseEntity<>(logs, HttpStatus.OK);
//        }
//    }
//
//    public ResponseEntity<?> getLogById(UUID id) {
//        Optional<AuditLog> log = auditLogRepository.findById(id);
//        if (log.isPresent()) {
//            return new ResponseEntity<>(log.get(), HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>("Audit log not found", HttpStatus.NOT_FOUND);
//        }
//    }
//
//    public ResponseEntity<?> updateAuditLog(UUID id, AuditLog updatedLog) {
//        Optional<AuditLog> existingLog = auditLogRepository.findById(id);
//        if (existingLog.isPresent()) {
//            AuditLog log = existingLog.get();
//
//            log.setAction(updatedLog.getAction());
//            log.setEntity(updatedLog.getEntity());
//            log.setEntityId(updatedLog.getEntityId());
//            log.setRemarks(updatedLog.getRemarks());
//            log.setTimestamp(LocalDateTime.now());
//
//            if (updatedLog.getUser() != null && updatedLog.getUser().getEmployeeId() != null) {
//                Optional<Employee> user = employeeRepository.findById(updatedLog.getUser().getEmployeeId());
//                user.ifPresent(log::setUser);
//            }
//
//            AuditLog saved = auditLogRepository.save(log);
//            return new ResponseEntity<>(saved, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>("Audit log not found", HttpStatus.NOT_FOUND);
//        }
//    }
//
//    public ResponseEntity<?> deleteLog(UUID id) {
//        Optional<AuditLog> log = auditLogRepository.findById(id);
//        if (log.isPresent()) {
//            auditLogRepository.deleteById(id);
//            return new ResponseEntity<>("Audit log deleted successfully", HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>("Audit log not found", HttpStatus.NOT_FOUND);
//        }
//    }
//
//
//    public ResponseEntity<?> getLogsByUserId(UUID userId) {
//        List<AuditLog> logs = auditLogRepository.findByUser_EmployeeId(userId);
//        if (logs.isEmpty()) {
//            return new ResponseEntity<>("No audit logs for this user", HttpStatus.NOT_FOUND);
//        } else {
//            return new ResponseEntity<>(logs, HttpStatus.OK);
//        }
//    }
//
//
////    public ResponseEntity<?> getLogsByUserId(Long userId) {
////        Optional<AuditLog> logs = auditLogRepository.findById(userId);
////        if (logs.isEmpty()) {
////            return new ResponseEntity<>("No audit logs for this user", HttpStatus.NOT_FOUND);
////        } else {
////            return new ResponseEntity<>(logs, HttpStatus.OK);
////        }
////    }
//}
//

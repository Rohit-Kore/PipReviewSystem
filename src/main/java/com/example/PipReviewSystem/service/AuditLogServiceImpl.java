package com.example.PipReviewSystem.service;

import com.example.PipReviewSystem.entity.AuditLog;
import com.example.PipReviewSystem.entity.Employee;
import com.example.PipReviewSystem.enums.Role; // Added for ADMIN role lookup
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

    @Autowired // Added: NotificationService injection
    private NotificationService notificationService;

    /**
     * Adds a new audit log entry to the system.
     * Sets the timestamp to the current time before saving.
     *
     * @param log The AuditLog entity to be saved.
     * @return ResponseEntity with the created AuditLog or a NOT_FOUND status if the user is not found.
     */
    @Override
    public ResponseEntity<?> addAuditLog(AuditLog log) {
        // Ensure the Employee object for the log entry exists in the database
        Optional<Employee> employee = employeeRepository.findById(log.getUser().getEmployeeId());
        if (employee.isPresent()) {
            log.setUser(employee.get()); // Set the managed Employee entity
            log.setTimestamp(LocalDateTime.now()); // Set current timestamp
            AuditLog saved = auditLogRepository.save(log);


            // Notification: Send alert to all ADMINs about a new audit log entry (ADDED)
            List<Employee> admins = employeeRepository.findByRole(Role.ADMIN); // Retrieve all employees with ADMIN role
            String adminNotificationTitle = "New Audit Log Entry";
            String adminNotificationMessage = "A new audit log entry has been created." +
                    " Action: " + saved.getAction() +
                    ", Entity: " + saved.getEntity() +
                    ", Entity ID: " + saved.getEntityId() +
                    " by " + (saved.getUser() != null ? saved.getUser().getName() : "Unknown User") + ".";

            for (Employee admin : admins) {
                notificationService.createNotification(admin, adminNotificationTitle, adminNotificationMessage, "INFO"); // INFO or ALERT depending on criticality
            }
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieves all audit log entries from the system.
     *
     * @return ResponseEntity with a list of all AuditLog entries.
     */
    @Override
    public ResponseEntity<?> getAllLogs() {
        List<AuditLog> logs = auditLogRepository.findAll();
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    /**
     * Retrieves audit log entries filtered by a specific user ID.
     *
     * @param userId The UUID of the user whose audit logs are to be retrieved.
     * @return ResponseEntity with a list of AuditLog entries for the specified user.
     */
    @Override
    public ResponseEntity<?> getLogsByUserId(UUID userId) {
        List<AuditLog> logs = auditLogRepository.findByUser_EmployeeId(userId);
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    /**
     * Deletes a specific audit log entry by its ID.
     * Sends a notification to all ADMINs when an audit log is deleted.
     *
     * @param logId The UUID of the audit log entry to delete.
     * @return ResponseEntity with a success message or a NOT_FOUND status if the log is not found.
     */
    @Override
    public ResponseEntity<?> deleteLog(UUID logId) {
        Optional<AuditLog> logToDeleteOpt = auditLogRepository.findById(logId); // Fetch the log before deleting
        if (logToDeleteOpt.isPresent()) {
            AuditLog deletedLog = logToDeleteOpt.get(); // Get the log object for notification details

            auditLogRepository.delete(deletedLog); // Perform the actual deletion

            // Notification: Send alert to all ADMINs about audit log deletion (ADDED)
            List<Employee> admins = employeeRepository.findByRole(Role.ADMIN); // Retrieve all employees with ADMIN role
            String adminNotificationTitle = "Audit Log Deleted";
            String adminNotificationMessage = "Audit Log ID: " + deletedLog.getLogId() +
                    " (Entity: " + deletedLog.getEntity() +
                    ", Action: " + deletedLog.getAction() +
                    ") has been deleted from the system by " +
                    (deletedLog.getUser() != null ? deletedLog.getUser().getName() : "Unknown User") + ".";

            for (Employee admin : admins) {
                notificationService.createNotification(admin, adminNotificationTitle, adminNotificationMessage, "ALERT");
            }

            return new ResponseEntity<>("Log deleted", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Log not found", HttpStatus.NOT_FOUND);
        }
    }
}






//package com.example.PipReviewSystem.service;
//
//import com.example.PipReviewSystem.entity.AuditLog;
//import com.example.PipReviewSystem.entity.Employee;
//import com.example.PipReviewSystem.enums.Role;
//import com.example.PipReviewSystem.repository.AuditLogRepository;
//import com.example.PipReviewSystem.repository.EmployeeRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//public class AuditLogServiceImpl implements AuditLogService {
//
//    @Autowired
//    private AuditLogRepository auditLogRepository;
//
//    @Autowired
//    private EmployeeRepository employeeRepository;
//
//    @Autowired // Added: NotificationService injection
//    private NotificationService notificationService;
//
//    @Override
//    public ResponseEntity<?> addAuditLog(AuditLog log) {
//        Optional<Employee> employee = employeeRepository.findById(log.getUser().getEmployeeId());
//        if (employee.isPresent()) {
//            log.setUser(employee.get());
//            log.setTimestamp(LocalDateTime.now());
//            AuditLog saved = auditLogRepository.save(log);
//
//            // Notification: Send alert to all ADMINs about a new audit log entry (ADDED)
//            List<Employee> admins = employeeRepository.findByRole(Role.ADMIN); // Retrieve all employees with ADMIN role
//            String adminNotificationTitle = "New Audit Log Entry";
//            String adminNotificationMessage = "A new audit log entry has been created." +
//                    " Action: " + saved.getAction() +
//                    ", Entity: " + saved.getEntity() +
//                    ", Entity ID: " + saved.getEntityId() +
//                    " by " + (saved.getUser() != null ? saved.getUser().getName() : "Unknown User") + ".";
//
//            for (Employee admin : admins) {
//                notificationService.createNotification(admin, adminNotificationTitle, adminNotificationMessage, "INFO"); // INFO or ALERT depending on criticality
//            }
//            return new ResponseEntity<>(saved, HttpStatus.CREATED);
//
//
//        } else {
//
//            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
//        }
//    }
//
//    @Override
//    public ResponseEntity<?> getAllLogs() {
//        List<AuditLog> logs = auditLogRepository.findAll();
//        return new ResponseEntity<>(logs, HttpStatus.OK);
//    }
//
//    @Override
//    public ResponseEntity<?> getLogsByUserId(UUID userId) {
//        List<AuditLog> logs = auditLogRepository.findByUser_EmployeeId(userId);
//        return new ResponseEntity<>(logs, HttpStatus.OK);
//    }
//
//    @Override
//    public ResponseEntity<?> deleteLog(UUID logId) {
//        Optional<AuditLog> log = auditLogRepository.findById(logId);
//        if (log.isPresent()) {
//            auditLogRepository.delete(log.get());
//
//
//            return new ResponseEntity<>("Log deleted", HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>("Log not found", HttpStatus.NOT_FOUND);
//        }
//    }
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//

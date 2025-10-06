package com.example.PipReviewSystem.controller;

import com.example.PipReviewSystem.entity.Notification;
import com.example.PipReviewSystem.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // API to get all notifications for a user by their ID
    @GetMapping("/user/id/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<List<Notification>> getNotificationsByUserId(@PathVariable UUID userId) {
        List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    // API to get unread notifications for a user by their ID
    @GetMapping("/user/id/{userId}/unread")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<List<Notification>> getUnreadNotificationsByUserId(@PathVariable UUID userId) {
        List<Notification> notifications = notificationService.getUnreadNotificationsByUserId(userId);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    // API to mark a notification as read
    @PutMapping("/{notificationId}/mark-as-read")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long notificationId) {
        notificationService.markNotificationAsRead(notificationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // API to mark all notifications as read for a user
    @PutMapping("/user/id/{userId}/mark-all-as-read")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Void> markAllNotificationsAsRead(@PathVariable UUID userId) {
        notificationService.markAllNotificationsAsRead(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ✅ API to get notifications by email
    @GetMapping("/user/email/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<List<Notification>> getNotificationsByUserEmail(@PathVariable String email) {
        List<Notification> notifications = notificationService.getNotificationsByUserEmail(email);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

}




























//package com.example.PipReviewSystem.controller;
//
//
//import com.example.PipReviewSystem.entity.Notification;
//import com.example.PipReviewSystem.service.NotificationService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.UUID;
//
//@CrossOrigin("*")
//@RestController
//@RequestMapping("/api/notifications")
//@RequiredArgsConstructor
//public class NotificationController {
//
//    private final NotificationService notificationService;
//
//    // API to get all notifications for a user by their ID
//    // Only the relevant employee, their Manager, HR, or Admin can view these notifications
//    @GetMapping("/user/{userId}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE') and " +
//            "(#userId == authentication.principal.employeeId or " + // If it's their own ID
//            "hasRole('ADMIN') or hasRole('HR') or " +
//            "(@employeeRepository.findById(#userId).orElse(null)?.manager?.employeeId == authentication.principal.employeeId))") // If it's their manager
//    public ResponseEntity<List<Notification>> getNotificationsByUserId(@PathVariable UUID userId) {
//        List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
//        return new ResponseEntity<>(notifications, HttpStatus.OK);
//    }
//
//    // API to get unread notifications for a user by their ID
//    // Only the relevant employee, their Manager, HR, or Admin can view these notifications
//    @GetMapping("/user/{userId}/unread")
//    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE') and " +
//            "(#userId == authentication.principal.employeeId or " +
//            "hasRole('ADMIN') or hasRole('HR') or " +
//            "(@employeeRepository.findById(#userId).orElse(null)?.manager?.employeeId == authentication.principal.employeeId))")
//    public ResponseEntity<List<Notification>> getUnreadNotificationsByUserId(@PathVariable UUID userId) {
//        List<Notification> notifications = notificationService.getUnreadNotificationsByUserId(userId);
//        return new ResponseEntity<>(notifications, HttpStatus.OK);
//    }
//
//    // API to mark a notification as read
//    // Only the employee, manager, HR, or admin can do this
//    @PutMapping("/{notificationId}/mark-as-read")
//    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')") // TODO: Ensure the employee can only mark their own notifications
//    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long notificationId) {
//        notificationService.markNotificationAsRead(notificationId);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
//
//    // API to mark all notifications as read for a user
//    // Only the employee, manager, HR, or admin can do this
//    @PutMapping("/user/{userId}/mark-all-as-read")
//    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')") // TODO: Ensure the employee can only mark their own notifications
//    public ResponseEntity<Void> markAllNotificationsAsRead(@PathVariable UUID userId) {
//        notificationService.markAllNotificationsAsRead(userId);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
//
//
//    @GetMapping("/user/{email}")
//
//    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
//
//    public ResponseEntity<List<Notification>> getNotificationsByUserEmail(@PathVariable String email) {
//
//        List<Notification> notifications = notificationService.getNotificationsByUserEmail(email);
//
//        return new ResponseEntity<>(notifications, HttpStatus.OK);
//
//    }
//
//
//}

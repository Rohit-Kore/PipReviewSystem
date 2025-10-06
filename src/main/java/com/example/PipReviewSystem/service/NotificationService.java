package com.example.PipReviewSystem.service;

import com.example.PipReviewSystem.entity.Employee;
import com.example.PipReviewSystem.entity.Notification;
import com.example.PipReviewSystem.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Creates and saves a new notification, then sends it via WebSocket to the targeted employee.
     *
     * @param employee The employee for whom the notification is intended.
     * @param title The title of the notification.
     * @param message The main message of the notification.
     * @param type The type of notification (e.g., INFO, ALERT, REMINDER).
     */
    public void createNotification(Employee employee, String title, String message, String type) {
        Notification notification = new Notification();
        notification.setEmployee(employee);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setIsRead(false); // Notifications are unread by default
        notification.setTimestamp(LocalDateTime.now()); // Set the current timestamp

        Notification savedNotification = notificationRepository.save(notification); // Save to database


        messagingTemplate.convertAndSendToUser(
                employee.getEmployeeId().toString(), // User ID as String
                "/queue/notifications",              // Destination for user-specific notifications
                savedNotification                    // The notification object to send
        );
    }

    /**
     * Retrieves all notifications for a given user ID
     * @param userId The UUID of the employee.
     * @return A list of Notification entities for the specified user.
     */
    public List<Notification> getNotificationsByUserId(UUID userId) {
        return notificationRepository.findByEmployeeEmployeeId(userId);
    }


    public List<Notification> getUnreadNotificationsByUserId(UUID userId) {
        return notificationRepository.findByIsReadFalseAndEmployeeEmployeeId(userId);
    }

    /**
     * Marks a single notification as read by its ID.
     *
     * @param notificationId The ID of the notification to mark as read.
     * @throws RuntimeException if the notification is not found.
     */
    public void markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + notificationId));

        if (!notification.getIsRead()) { // avoid duplicate updates
            notification.setIsRead(true);
            notificationRepository.save(notification);

            // optional: update real-time for UI
            messagingTemplate.convertAndSendToUser(
                    notification.getEmployee().getEmployeeId().toString(),
                    "/queue/notifications/read",
                    notification
            );
        }
    }

    /**
     * Marks all unread notifications for a specific user as read.
     *
     * @param userId The UUID of the employee whose notifications should be marked as read.
     */
    public void markAllNotificationsAsRead(UUID userId) {
        List<Notification> unreadNotifications = notificationRepository.findByIsReadFalseAndEmployeeEmployeeId(userId);
        unreadNotifications.forEach(notification -> notification.setIsRead(true)); // Iterate and mark each as read
        notificationRepository.saveAll(unreadNotifications); // Save all updated notifications in a batch
    }

    /**
     * Deletes a notification by its ID.
     *
     * @param notificationId The ID of the notification to delete.
     * @throws RuntimeException if the notification is not found.
     */
    public void deleteNotification(Long notificationId) { // ADDED: Method to delete a notification
        if (!notificationRepository.existsById(notificationId)) {
            throw new RuntimeException("Notification not found with ID: " + notificationId);
        }
        notificationRepository.deleteById(notificationId);
    }

    public List<Notification> getNotificationsByUserEmail(String email) {
        return notificationRepository.findByEmployeeEmail(email);
    }

    public List<Notification> getUnreadNotificationsByUserEmail(String email) {
        List<Notification> allNotifications = notificationRepository.findByEmployeeEmail(email);
        return allNotifications.stream()
                .filter(notification -> !Boolean.TRUE.equals(notification.getIsRead()))
                .toList();
    }


    public void sendNotificationToEmployee(String email, Notification notification) {

        messagingTemplate.convertAndSendToUser(

                email,

                "/queue/notifications",

                notification

        );

    }


}

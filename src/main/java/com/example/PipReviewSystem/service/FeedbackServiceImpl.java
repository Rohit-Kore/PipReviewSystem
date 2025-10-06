package com.example.PipReviewSystem.service;

import com.example.PipReviewSystem.dto.FeedbackRequestDTO;
import com.example.PipReviewSystem.entity.Feedback;
import com.example.PipReviewSystem.repository.EmployeeRepository;
import com.example.PipReviewSystem.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.PipReviewSystem.entity.Employee;
import com.example.PipReviewSystem.enums.Role; // Added for ADMIN role lookup

import java.util.List;
import java.util.Objects; // Added for Objects.equals comparison
import java.util.Optional;
import java.util.UUID;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private NotificationService notificationService; // NotificationService injected

    /**
     * Creates new feedback based on the provided DTO.
     * Sets the 'toUser' and 'fromUser' based on UUID and authenticated email respectively.
     * Sends a notification to the 'toUser' (recipient) when new feedback is received.
     *
     * @param dto The FeedbackRequestDTO containing feedback details.
     * @param fromUserEmail The email of the user giving the feedback (authenticated user).
     * @return The created Feedback entity.
     * @throws RuntimeException if 'toUser' or 'fromUser' are not found.
     */
    @Override
    public Feedback createFeedbackFromDTO(FeedbackRequestDTO dto, String fromUserEmail) {
        Feedback feedback = new Feedback();
        feedback.setFeedbackType(dto.getFeedbackType());
        feedback.setComments(dto.getComments());
        feedback.setRating(dto.getRating());
        feedback.setIsAnonymous(dto.getIsAnonymous());
        feedback.setCreatedDate(java.time.LocalDateTime.now());

        // Set toUser (recipient of the feedback)
        Employee toUser = employeeRepository.findByEmployeeId(dto.getToUserId())
                .orElseThrow(() -> new RuntimeException("To user not found"));
        feedback.setToUser(toUser);

        // Set fromUser (giver of the feedback) using authenticated email
        Employee fromUser = employeeRepository.findByEmail(fromUserEmail)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
        feedback.setFromUser(fromUser);

        Feedback savedFeedback = feedbackRepository.save(feedback);

        // Notification call: New feedback received (ADDED)
        String notificationTitle = "New Feedback Received";
        String notificationMessage = "You have received new feedback from " + fromUser.getName();
        // If feedback is anonymous, the sender's name shouldn't be revealed
        if (savedFeedback.getIsAnonymous() != null && savedFeedback.getIsAnonymous()) {
            notificationMessage = "You have received new anonymous feedback.";
        }
        notificationService.createNotification(savedFeedback.getToUser(), notificationTitle, notificationMessage, "ALERT");

        return savedFeedback;
    }

    /**
     * Retrieves all feedback records from the database.
     *
     * @return A list of all Feedback entities.
     */
    @Override
    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }

    /**
     * Retrieves a single feedback record by its ID.
     *
     * @param id The ID of the feedback to retrieve.
     * @return An Optional containing the Feedback entity if found, or empty otherwise.
     */
    @Override
    public Optional<Feedback> getFeedbackById(Long id) {
        return feedbackRepository.findById(id);
    }

    /**
     * Updates an existing feedback record.
     * Only allows updates to specific fields (type, comments, rating, isAnonymous).
     * Sends notifications to both the 'toUser' and 'fromUser' if changes are made.
     *
     * @param id The ID of the feedback to update.
     * @param updatedFeedback The Feedback entity with updated details.
     * @return The updated Feedback entity.
     * @throws RuntimeException if the feedback is not found.
     */
    @Override
    public Feedback updateFeedback(Long id, Feedback updatedFeedback) {
        Feedback existing = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));

        boolean changesMade = false; // Flag to track if any changes occurred

        // Only update allowed fields and check if changes were made
        if (!Objects.equals(existing.getFeedbackType(), updatedFeedback.getFeedbackType())) {
            existing.setFeedbackType(updatedFeedback.getFeedbackType());
            changesMade = true;
        }
        if (!Objects.equals(existing.getComments(), updatedFeedback.getComments())) {
            existing.setComments(updatedFeedback.getComments());
            changesMade = true;
        }
        if (existing.getRating() != updatedFeedback.getRating()) { // Primitive int comparison
            existing.setRating(updatedFeedback.getRating());
            changesMade = true;
        }
        if (!Objects.equals(existing.getIsAnonymous(), updatedFeedback.getIsAnonymous())) {
            existing.setIsAnonymous(updatedFeedback.getIsAnonymous());
            changesMade = true;
        }

        Feedback savedFeedback = feedbackRepository.save(existing);

        // Notification call: Feedback updated (ADDED)
        if (changesMade) { // Only send notifications if actual changes were made
            String notificationTitle = "Feedback Updated";
            String baseMessage = "Feedback you gave has been updated.";

            // Notification to the user who received the feedback ('toUser')
            notificationService.createNotification(
                    savedFeedback.getToUser(),
                    notificationTitle,
                    "Feedback you received from " + savedFeedback.getFromUser().getName() + " has been updated.",
                    "INFO"
            );

            // Notification to the user who gave the feedback ('fromUser')
            notificationService.createNotification(
                    savedFeedback.getFromUser(),
                    notificationTitle,
                    baseMessage + " (to " + savedFeedback.getToUser().getName() + ").",
                    "INFO"
            );
        }

        return savedFeedback;
    }

    /**
     * Deletes a feedback record by its ID.
     * Sends notifications to the 'toUser' (recipient), 'fromUser' (giver), and all ADMINs.
     *
     * @param id The ID of the feedback to delete.
     * @throws RuntimeException if the feedback is not found.
     */
    @Override
    public void deleteFeedback(Long id) {
        Feedback feedbackToDelete = feedbackRepository.findById(id) // Fetch entity before deleting for notification
                .orElseThrow(() -> new RuntimeException("Feedback not found with ID: " + id));

        feedbackRepository.deleteById(id);

        // Notification to the user who received the feedback ('toUser') (ADDED)
        String toUserNotifTitle = "Feedback Deleted";
        String toUserNotifMessage = "Feedback you received from " + feedbackToDelete.getFromUser().getName() + " has been deleted.";
        if (feedbackToDelete.getIsAnonymous() != null && feedbackToDelete.getIsAnonymous()) {
            toUserNotifMessage = "Anonymous feedback you received has been deleted.";
        }
        notificationService.createNotification(feedbackToDelete.getToUser(), toUserNotifTitle, toUserNotifMessage, "ALERT");

        // Notification to the user who gave the feedback ('fromUser') (ADDED)
        String fromUserNotifTitle = "Feedback Deleted";
        String fromUserNotifMessage = "Your feedback given to " + feedbackToDelete.getToUser().getName() + " has been deleted.";
        notificationService.createNotification(feedbackToDelete.getFromUser(), fromUserNotifTitle, fromUserNotifMessage, "ALERT");

        // Notification to all ADMINs about the deletion (ADDED)
        List<Employee> admins = employeeRepository.findByRole(Role.ADMIN);
        for (Employee admin : admins) {
            String adminNotifTitle = "Feedback Deleted (Admin)";
            String adminNotifMessage = "Feedback ID " + feedbackToDelete.getFeedbackId() + " (from " + feedbackToDelete.getFromUser().getName() + " to " + feedbackToDelete.getToUser().getName() + ") has been deleted.";
            notificationService.createNotification(admin, adminNotifTitle, adminNotifMessage, "ALERT");
        }
    }

    /**
     * Retrieves a list of feedback records given to a specific user.
     *
     * @param toUserId The UUID of the user who received the feedback.
     * @return A list of Feedback entities given to the specified user.
     */
    @Override
    public List<Feedback> getFeedbacksByToUserId(UUID toUserId) {
        return feedbackRepository.findByToUserEmployeeId(toUserId);
    }

    /**
     * Retrieves a list of feedback records given by a specific user.
     *
     * @param fromUserId The UUID of the user who gave the feedback.
     * @return A list of Feedback entities given by the specified user.
     */
    @Override
    public List<Feedback> getFeedbacksByFromUserId(UUID fromUserId) {
        return feedbackRepository.findByFromUserEmployeeId(fromUserId);
    }

    /**
     * Retrieves a list of feedback records based on their feedback type.
     *
     * @param type The type of feedback (e.g., "PEER", "SELF", "MANAGER").
     * @return A list of Feedback entities matching the specified type.
     */
    @Override
    public List<Feedback> getFeedbacksByType(String type) {
        return feedbackRepository.findByFeedbackType(type);
    }
}


//package com.example.PipReviewSystem.service;
//
//import com.example.PipReviewSystem.dto.FeedbackRequestDTO;
//import com.example.PipReviewSystem.entity.Feedback;
//import com.example.PipReviewSystem.repository.EmployeeRepository;
//import com.example.PipReviewSystem.repository.FeedbackRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;


//import com.example.PipReviewSystem.entity.Employee;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//@Service
//public class FeedbackServiceImpl implements FeedbackService {
//
//    @Autowired
//    private FeedbackRepository feedbackRepository;
//    @Autowired
//    private EmployeeRepository employeeRepository;
//
//
//    @Override
//    public Feedback createFeedbackFromDTO(FeedbackRequestDTO dto, String fromUserEmail) {
//        Feedback feedback = new Feedback();
//        feedback.setFeedbackType(dto.getFeedbackType());
//        feedback.setComments(dto.getComments());
//        feedback.setRating(dto.getRating());
//        feedback.setIsAnonymous(dto.getIsAnonymous());
//        feedback.setCreatedDate(java.time.LocalDateTime.now());
//
//        // Set toUser
//        Employee toUser = employeeRepository.findByEmployeeId(dto.getToUserId())
//                .orElseThrow(() -> new RuntimeException("To user not found"));
//        feedback.setToUser(toUser);
//
//        // Set fromUser using authenticated email
//        Employee fromUser = employeeRepository.findByEmail(fromUserEmail)
//                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
//        feedback.setFromUser(fromUser);
//
//        return feedbackRepository.save(feedback);
//    }
//
//
//    @Override
//    public List<Feedback> getAllFeedbacks() {
//        return feedbackRepository.findAll();
//    }
//
//    @Override
//    public Optional<Feedback> getFeedbackById(Long id) {
//        return feedbackRepository.findById(id);
//    }
//
//    @Override
//    public Feedback updateFeedback(Long id, Feedback updatedFeedback) {
//        Feedback existing = feedbackRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Feedback not found"));
//
//        // ✅ Only update allowed fields
//        existing.setFeedbackType(updatedFeedback.getFeedbackType());
//        existing.setComments(updatedFeedback.getComments());
//        existing.setRating(updatedFeedback.getRating());
//        existing.setIsAnonymous(updatedFeedback.getIsAnonymous());
//
//        return feedbackRepository.save(existing);
//    }
//
// /*   public Feedback updateFeedback(Long id, Feedback updatedFeedback) {
//        return feedbackRepository.findById(id).map(feedback -> {
//            feedback.setComments(updatedFeedback.getComments());
//            feedback.setRating(updatedFeedback.getRating());
//            feedback.setIsAnonymous(updatedFeedback.getIsAnonymous());
//            feedback.setFeedbackType(updatedFeedback.getFeedbackType());
//          *//*  feedback.setFromUser(updatedFeedback.getFromUser());
//            feedback.setToUser(updatedFeedback.getToUser());*//*
//            return feedbackRepository.save(feedback);
//
//        }).orElseThrow(() -> new RuntimeException("Feedback not found with ID: " + id));
//    }*/
//
//    @Override
//    public void deleteFeedback(Long id) {
//        feedbackRepository.deleteById(id);
//    }
//
//    @Override
//    public List<Feedback> getFeedbacksByToUserId(UUID toUserId) {
//        return feedbackRepository.findByToUserEmployeeId(toUserId);
//    }
//
//    @Override
//    public List<Feedback> getFeedbacksByFromUserId(UUID fromUserId) {
//        return feedbackRepository.findByFromUserEmployeeId(fromUserId);
//    }
//
//    @Override
//    public List<Feedback> getFeedbacksByType(String type) {
//        return feedbackRepository.findByFeedbackType(type);
//    }
//}

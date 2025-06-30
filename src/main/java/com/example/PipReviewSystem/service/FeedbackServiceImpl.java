package com.example.PipReviewSystem.service;

import com.example.PipReviewSystem.dto.FeedbackRequestDTO;
import com.example.PipReviewSystem.entity.Feedback;
import com.example.PipReviewSystem.repository.EmployeeRepository;
import com.example.PipReviewSystem.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.PipReviewSystem.entity.Employee;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private EmployeeRepository employeeRepository;


    @Override
    public Feedback createFeedbackFromDTO(FeedbackRequestDTO dto, String fromUserEmail) {
        Feedback feedback = new Feedback();
        feedback.setFeedbackType(dto.getFeedbackType());
        feedback.setComments(dto.getComments());
        feedback.setRating(dto.getRating());
        feedback.setAnonymous(dto.getIsAnonymous());
        feedback.setCreatedDate(java.time.LocalDateTime.now());

        // Set toUser
        Employee toUser = employeeRepository.findByEmployeeId(dto.getToUserId())
                .orElseThrow(() -> new RuntimeException("To user not found"));
        feedback.setToUser(toUser);

        // Set fromUser using authenticated email
        Employee fromUser = employeeRepository.findByEmail(fromUserEmail)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
        feedback.setFromUser(fromUser);

        return feedbackRepository.save(feedback);
    }


    @Override
    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }

    @Override
    public Optional<Feedback> getFeedbackById(Long id) {
        return feedbackRepository.findById(id);
    }

    @Override
    public Feedback updateFeedback(Long id, Feedback updatedFeedback) {
        return feedbackRepository.findById(id).map(feedback -> {
            feedback.setComments(updatedFeedback.getComments());
            feedback.setRating(updatedFeedback.getRating());
            feedback.setAnonymous(updatedFeedback.getAnonymous());
            feedback.setFeedbackType(updatedFeedback.getFeedbackType());
            feedback.setFromUser(updatedFeedback.getFromUser());
            feedback.setToUser(updatedFeedback.getToUser());
            return feedbackRepository.save(feedback);
        }).orElseThrow(() -> new RuntimeException("Feedback not found with ID: " + id));
    }

    @Override
    public void deleteFeedback(Long id) {
        feedbackRepository.deleteById(id);
    }

    @Override
    public List<Feedback> getFeedbacksByToUserId(UUID toUserId) {
        return feedbackRepository.findByToUserEmployeeId(toUserId);
    }

    @Override
    public List<Feedback> getFeedbacksByFromUserId(UUID fromUserId) {
        return feedbackRepository.findByFromUserEmployeeId(fromUserId);
    }

    @Override
    public List<Feedback> getFeedbacksByType(String type) {
        return feedbackRepository.findByFeedbackType(type);
    }
}

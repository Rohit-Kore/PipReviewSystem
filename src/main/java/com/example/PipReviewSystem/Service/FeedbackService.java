package com.example.PipReviewSystem.Service;

import com.example.PipReviewSystem.entity.Feedback;
import com.example.PipReviewSystem.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    public Feedback createFeedback(Feedback feedback) {
        feedback.setCreatedDate(java.time.LocalDateTime.now());
        return feedbackRepository.save(feedback);
    }

    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }

    public Optional<Feedback> getFeedbackById(Long id) {
        return feedbackRepository.findById(id);
    }

    public Feedback updateFeedback(Long id, Feedback updatedFeedback) {
        return feedbackRepository.findById(id).map(feedback -> {
            feedback.setComments(updatedFeedback.getComments());
            feedback.setRating(updatedFeedback.getRating());
            feedback.setIsAnonymous(updatedFeedback.getIsAnonymous());
            feedback.setFeedbackType(updatedFeedback.getFeedbackType());
            feedback.setFromUser(updatedFeedback.getFromUser());
            feedback.setToUser(updatedFeedback.getToUser());
            return feedbackRepository.save(feedback);
        }).orElseThrow(() -> new RuntimeException("Feedback not found with ID: " + id));
    }

    public void deleteFeedback(Long id) {
        feedbackRepository.deleteById(id);
    }

    public List<Feedback> getFeedbacksByToUserId(Long toUserId) {
        return feedbackRepository.findByToUserEmployeeId(toUserId);
    }

    public List<Feedback> getFeedbacksByFromUserId(Long fromUserId) {
        return feedbackRepository.findByFromUserEmployeeId(fromUserId);
    }

    public List<Feedback> getFeedbacksByType(String type) {
        return feedbackRepository.findByFeedbackType(type);
    }
}

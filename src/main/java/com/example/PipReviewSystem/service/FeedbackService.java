package com.example.PipReviewSystem.service;

import com.example.PipReviewSystem.dto.FeedbackRequestDTO;
import com.example.PipReviewSystem.entity.Feedback;

import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface FeedbackService {
    Feedback createFeedbackFromDTO(FeedbackRequestDTO dto, String fromUserEmail);

    List<Feedback> getAllFeedbacks();
    Optional<Feedback> getFeedbackById(Long id);
    Feedback updateFeedback(Long id, Feedback updatedFeedback);
    void deleteFeedback(Long id);
    List<Feedback> getFeedbacksByToUserId(UUID toUserId);
    List<Feedback> getFeedbacksByFromUserId(UUID fromUserId);
    List<Feedback> getFeedbacksByType(String type);
}

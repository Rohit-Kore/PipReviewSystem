package com.example.PipReviewSystem.Controller;

import com.example.PipReviewSystem.entity.Feedback;
import com.example.PipReviewSystem.Service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<Feedback> createFeedback(@RequestBody Feedback feedback) {
        return ResponseEntity.ok(feedbackService.createFeedback(feedback));
    }

    @GetMapping
    public ResponseEntity<List<Feedback>> getAllFeedbacks() {
        return ResponseEntity.ok(feedbackService.getAllFeedbacks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Feedback> getFeedbackById(@PathVariable Long id) {
        return feedbackService.getFeedbackById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Feedback> updateFeedback(@PathVariable Long id, @RequestBody Feedback feedback) {
        try {
            return ResponseEntity.ok(feedbackService.updateFeedback(id, feedback));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/toUser/{toUserId}")
    public ResponseEntity<List<Feedback>> getFeedbacksByToUser(@PathVariable Long toUserId) {
        return ResponseEntity.ok(feedbackService.getFeedbacksByToUserId(toUserId));
    }

    @GetMapping("/fromUser/{fromUserId}")
    public ResponseEntity<List<Feedback>> getFeedbacksByFromUser(@PathVariable Long fromUserId) {
        return ResponseEntity.ok(feedbackService.getFeedbacksByFromUserId(fromUserId));
    }

    @GetMapping("/type/{feedbackType}")
    public ResponseEntity<List<Feedback>> getFeedbacksByType(@PathVariable String feedbackType) {
        return ResponseEntity.ok(feedbackService.getFeedbacksByType(feedbackType));
    }
}

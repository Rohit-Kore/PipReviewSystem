package com.example.PipReviewSystem.controller;

import com.example.PipReviewSystem.dto.FeedbackRequestDTO;
import com.example.PipReviewSystem.dto.FeedbackResponseDto;
import com.example.PipReviewSystem.entity.Feedback;
import com.example.PipReviewSystem.repository.FeedbackRepository;
import com.example.PipReviewSystem.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private FeedbackRepository feedbackRepository;

    @PostMapping("/add")
    public ResponseEntity<Feedback> createFeedback(@RequestBody FeedbackRequestDTO dto, Principal principal) {
        return ResponseEntity.ok(feedbackService.createFeedbackFromDTO(dto, principal.getName()));
    }

    @GetMapping
    public List<FeedbackResponseDto> getAllFeedbacks() {
        return feedbackRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }


    @GetMapping("/{id}")
    public ResponseEntity<FeedbackResponseDto> getFeedbackById(@PathVariable Long id) {
        return feedbackService.getFeedbackById(id)
                .map(f -> ResponseEntity.ok(mapToDto(f)))
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
    public ResponseEntity<List<FeedbackResponseDto>> getFeedbacksByToUser(@PathVariable UUID toUserId) {
        return ResponseEntity.ok(
                feedbackService.getFeedbacksByToUserId(toUserId).stream()
                        .map(this::mapToDto)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/fromUser/{fromUserId}")
    public ResponseEntity<List<FeedbackResponseDto>> getFeedbacksByFromUser(@PathVariable UUID fromUserId) {
        return ResponseEntity.ok(
                feedbackService.getFeedbacksByFromUserId(fromUserId).stream()
                        .map(this::mapToDto)
                        .collect(Collectors.toList())
        );
    }


    @GetMapping("/type/{feedbackType}")
    public ResponseEntity<List<FeedbackResponseDto>> getFeedbacksByType(@PathVariable String feedbackType) {
        return ResponseEntity.ok(
                feedbackService.getFeedbacksByType(feedbackType).stream()
                        .map(this::mapToDto)
                        .collect(Collectors.toList())
        );
    }


    public FeedbackResponseDto mapToDto(Feedback feedback) {
        FeedbackResponseDto dto = new FeedbackResponseDto();
        dto.setFeedbackId(feedback.getFeedbackId());
        dto.setFromUser(feedback.getFromUser().getEmployeeId());
        dto.setToUser(feedback.getToUser().getEmployeeId());
        dto.setFeedbackType(feedback.getFeedbackType());
        dto.setComments(feedback.getComments());
        dto.setRating(feedback.getRating());
        dto.setAnonymous(feedback.getAnonymous());
        dto.setCreatedDate(feedback.getCreatedDate());
        return dto;
    }

}

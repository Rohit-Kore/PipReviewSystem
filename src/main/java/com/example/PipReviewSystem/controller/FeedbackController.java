package com.example.PipReviewSystem.controller;

import com.example.PipReviewSystem.dto.FeedbackRequestDTO;
import com.example.PipReviewSystem.dto.FeedbackResponseDto;
import com.example.PipReviewSystem.entity.Feedback;
import com.example.PipReviewSystem.repository.FeedbackRepository;
import com.example.PipReviewSystem.service.FeedbackService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Operation(
            summary = "Step 1️⃣ - Create Feedback",
            description = "Allows EMPLOYEE or MANAGER to create feedback for another user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Feedback created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or user not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PreAuthorize("hasAnyAuthority('EMPLOYEE', 'MANAGER')")
    @PostMapping("/add")
    public ResponseEntity<Feedback> createFeedback(@RequestBody FeedbackRequestDTO dto, Principal principal) {
        return ResponseEntity.ok(feedbackService.createFeedbackFromDTO(dto, principal.getName()));
    }

    @Operation(
            summary = "Step 2️⃣ - Get All Feedbacks",
            description = "HR and MANAGER can view all feedbacks."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of feedbacks retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyAuthority('HR', 'MANAGER')")
    @GetMapping
    public List<FeedbackResponseDto> getAllFeedbacks() {
        return feedbackRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Operation(
            summary = "Step 3️⃣ - Get Feedback by ID",
            description = "Allows EMPLOYEE (owner), MANAGER, or HR to fetch a feedback by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Feedback found"),
            @ApiResponse(responseCode = "404", description = "Feedback not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyAuthority('HR', 'MANAGER', 'EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<FeedbackResponseDto> getFeedbackById(@PathVariable Long id) {
        return feedbackService.getFeedbackById(id)
                .map(f -> ResponseEntity.ok(mapToDto(f)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Step 4️⃣ - Update Feedback",
            description = "Allows MANAGER or HR to update feedback details."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Feedback updated successfully"),
            @ApiResponse(responseCode = "404", description = "Feedback not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyAuthority('MANAGER', 'HR')")
    @PutMapping("/{id}")
    public ResponseEntity<Feedback> updateFeedback(@PathVariable Long id, @RequestBody Feedback feedback) {
        try {
            return ResponseEntity.ok(feedbackService.updateFeedback(id, feedback));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Step 5️⃣ - Delete Feedback",
            description = "Allows HR only to delete a feedback."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Feedback deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Feedback not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAuthority('HR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Step 6️⃣ - Get Feedbacks Received By User",
            description = "Allows EMPLOYEE (self), MANAGER, or HR to see feedbacks received by a user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Feedbacks fetched successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyAuthority('HR', 'MANAGER', 'EMPLOYEE')")
    @GetMapping("/toUser/{toUserId}")
    public ResponseEntity<List<FeedbackResponseDto>> getFeedbacksByToUser(@PathVariable UUID toUserId) {
        return ResponseEntity.ok(
                feedbackService.getFeedbacksByToUserId(toUserId).stream()
                        .map(this::mapToDto)
                        .collect(Collectors.toList())
        );
    }

    @Operation(
            summary = "Step 7️⃣ - Get Feedbacks Given By User",
            description = "Allows EMPLOYEE (self), MANAGER, or HR to see feedbacks given by a user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Feedbacks fetched successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyAuthority('HR', 'MANAGER', 'EMPLOYEE')")
    @GetMapping("/fromUser/{fromUserId}")
    public ResponseEntity<List<FeedbackResponseDto>> getFeedbacksByFromUser(@PathVariable UUID fromUserId) {
        return ResponseEntity.ok(
                feedbackService.getFeedbacksByFromUserId(fromUserId).stream()
                        .map(this::mapToDto)
                        .collect(Collectors.toList())
        );
    }

    @Operation(
            summary = "Step 8️⃣ - Get Feedbacks by Type",
            description = "Allows HR or MANAGER to filter feedbacks by type (PEER, MANAGER, SELF)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Feedbacks fetched successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyAuthority('HR', 'MANAGER')")
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
        dto.setIsAnonymous(feedback.getIsAnonymous());
        dto.setCreatedDate(feedback.getCreatedDate());
        return dto;
    }
}

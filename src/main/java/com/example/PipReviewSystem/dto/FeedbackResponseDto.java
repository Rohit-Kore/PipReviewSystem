package com.example.PipReviewSystem.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
public class FeedbackResponseDto {
    private Long feedbackId;
    private UUID fromUser;
    private String fromUserName;

    private UUID toUser;
    private String toUserName;
    private String feedbackType;
    private String comments;
    private int rating;
    private Boolean isAnonymous;
    private LocalDateTime createdDate;

    // Getters and setters
}

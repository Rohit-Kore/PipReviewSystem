package com.example.PipReviewSystem.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class FeedbackRequestDTO {
    private UUID toUserId;
    private String feedbackType;
    private String comments;
    private int rating;
    private Boolean isAnonymous;

}

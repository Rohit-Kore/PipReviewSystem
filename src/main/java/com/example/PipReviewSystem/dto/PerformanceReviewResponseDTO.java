package com.example.PipReviewSystem.dto;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PerformanceReviewResponseDTO {
    private Long reviewId;
    private UUID employeeId;
    private String employeeName;
    private UUID reviewerId;
    private String reviewerName;
    private String reviewPeriod;
    private LocalDateTime reviewDate;
    private String scores;
    private Double overallRating;
    private String comments;
    private String reviewType;
    private String pdfUrl;
}

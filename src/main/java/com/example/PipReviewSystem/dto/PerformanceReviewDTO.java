package com.example.PipReviewSystem.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PerformanceReviewDTO {
    private Long employeeId;
    private Long reviewerId;
    private String reviewPeriod;
    private LocalDateTime reviewDate;
    private String scores;  // JSON string
    private Double overallRating;
    private String comments;
    private String reviewType;
    private String pdfUrl;
}

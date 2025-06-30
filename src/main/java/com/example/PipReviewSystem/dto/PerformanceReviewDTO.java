package com.example.PipReviewSystem.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PerformanceReviewDTO {
    private UUID employeeId;
    private UUID reviewerId;
    private String reviewPeriod;
    private LocalDateTime reviewDate;
    private String scores;
    private Double overallRating;
    private String comments;
    private String reviewType;
    private String pdfUrl;
}



























//package com.example.PipReviewSystem.dto;
//
//import lombok.Data;
//
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//@Data
//public class PerformanceReviewDTO {
//    private UUID employeeId;
//    private UUID reviewerId;
//    private String reviewPeriod;
//    private LocalDateTime reviewDate;
//    private String scores;  // JSON string
//    private Double overallRating;
//    private String comments;
//    private String reviewType;
//    private String pdfUrl;
//}

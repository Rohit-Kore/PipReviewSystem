package com.example.PipReviewSystem.dto;

/*
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
*/




import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor // Added for convenience
@AllArgsConstructor
@Getter
@Setter// Added for convenience
public class PerformanceReviewDTO {
    private Long reviewId;
    private UUID employeeId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String employeeName;
    private UUID reviewerId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String reviewerName;
    private String reviewPeriod;
    private LocalDateTime reviewDate;
    private String scores;
    private Double overallRating;
    private String comments;
    private String reviewType;
    private String pdfUrl;
}

























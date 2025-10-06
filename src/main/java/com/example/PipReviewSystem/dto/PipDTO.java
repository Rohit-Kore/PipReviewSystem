package com.example.PipReviewSystem.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PipDTO {
    private Long pipId;

    private UUID employeeId;
    private String employeeName;

    private UUID reviewerId;
    private String reviewerName;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String goals;
    private String progress;
    private String status;
    private String outcome;
    private String comments;
}

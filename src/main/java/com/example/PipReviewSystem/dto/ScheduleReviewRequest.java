package com.example.PipReviewSystem.dto;

import lombok.Data;

@Data
public class ScheduleReviewRequest {
    private String employeeId;    // UUID string
    private String reviewDate;    // ISO-8601 e.g. "2025-09-10T11:00:00"
    private String scheduledBy;   // manager email
    private boolean notifyNow;    // optional: immediate notify
}

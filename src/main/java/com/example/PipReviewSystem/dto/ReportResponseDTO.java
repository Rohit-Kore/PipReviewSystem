package com.example.PipReviewSystem.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ReportResponseDTO {
    private Long reportId;
    private String reportType;
    private String fileUrl;
    private LocalDateTime generatedOn;

    private UUID createdById;
    private String createdByName;

    private UUID targetEmployeeId;
    private String targetEmployeeName;
}



// old  9/7   omkar
//public class ReportResponseDTO {
//    private Long reportId;
//    private String reportType;
//    private String fileUrl;
//    private LocalDateTime generatedOn;
//    private UUID createdById;
//    private String createdByName;
//    private String createdByEmail;
//
//    // Getters and setters
//
//}

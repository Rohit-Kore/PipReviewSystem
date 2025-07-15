package com.example.PipReviewSystem.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ReportRequestDTO {
    private UUID targetEmployeeId;
    private String reportType;
    private String fileUrl;
}


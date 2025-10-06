package com.example.PipReviewSystem.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

//@Getter
//@Setter
public class ReportRequestDTO {
    private UUID targetEmployeeId;
    private String reportType;
    private String fileUrl;


    public UUID getTargetEmployeeId() {
        return targetEmployeeId;
    }

    public void setTargetEmployeeId(UUID targetEmployeeId) {
        this.targetEmployeeId = targetEmployeeId;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}


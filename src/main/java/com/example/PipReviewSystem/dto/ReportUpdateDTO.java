package com.example.PipReviewSystem.dto;


import lombok.Getter;
import lombok.Setter;

//@Getter
//@Setter

public class ReportUpdateDTO {
    private String reportType;
    private String fileUrl;

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

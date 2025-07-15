package com.example.PipReviewSystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private Employee createdBy; // Manager who created

    @ManyToOne
    @JoinColumn(name = "target_employee_id")
    private Employee targetEmployee; // For whom report is created

    private String reportType;
    private String fileUrl;
    private LocalDateTime generatedOn;

}






//old commented on 9/7  cause testing bugs..

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long reportId;
//
//    @ManyToOne
//    @JoinColumn(name = "created_by")
//    private Employee createdBy;
//
//    private String reportType; // PERFORMANCE, PIP, FEEDBACK
//    private LocalDateTime generatedOn;
//
//    public String getFileUrl() {
//        return fileUrl;
//    }
//
//    public void setFileUrl(String fileUrl) {
//        this.fileUrl = fileUrl;
//    }
//
//    private String fileUrl; // Uncomment if file URL support is added
//
//
//    public Long getReportId() {
//        return reportId;
//    }
//
//    public void setReportId(Long reportId) {
//        this.reportId = reportId;
//    }
//
//    public Employee getCreatedBy() {
//        return createdBy;
//    }
//
//    public void setCreatedBy(Employee createdBy) {
//        this.createdBy = createdBy;
//    }
//
//    public String getReportType() {
//        return reportType;
//    }
//
//    public void setReportType(String reportType) {
//        this.reportType = reportType;
//    }
//
//    public LocalDateTime getGeneratedOn() {
//        return generatedOn;
//    }
//
//    public void setGeneratedOn(LocalDateTime generatedOn) {
//        this.generatedOn = generatedOn;
//    }
//}

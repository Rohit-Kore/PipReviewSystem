package com.example.PipReviewSystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "employeeId")
    @JsonIgnore
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "reviewer_id", referencedColumnName = "employeeId")
    private Employee reviewer;

    private String reviewPeriod; // e.g., Q1-2025
    private LocalDateTime reviewDate;

    @Column(columnDefinition = "TEXT")
    private String scores; // JSON like {"communication":4, "technical":3}

    private Double overallRating;
    private String comments;
    private String reviewType; // Monthly, Quarterly
    private String pdfUrl;

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Employee getReviewer() {
        return reviewer;
    }

    public void setReviewer(Employee reviewer) {
        this.reviewer = reviewer;
    }

    public String getReviewPeriod() {
        return reviewPeriod;
    }

    public void setReviewPeriod(String reviewPeriod) {
        this.reviewPeriod = reviewPeriod;
    }

    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getScores() {
        return scores;
    }

    public void setScores(String scores) {
        this.scores = scores;
    }

    public Double getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(Double overallRating) {
        this.overallRating = overallRating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getReviewType() {
        return reviewType;
    }

    public void setReviewType(String reviewType) {
        this.reviewType = reviewType;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }
}














//package com.example.PipReviewSystem.entity;
//
//import jakarta.persistence.*;
//
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//@Entity
//public class PerformanceReview {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long reviewId;
//
//    @ManyToOne
//    @JoinColumn(name = "employee_id", referencedColumnName = "employeeId")
//    private Employee employee;
//
//    @ManyToOne
//    @JoinColumn(name = "reviewer_id", referencedColumnName = "employeeId")
//    private Employee reviewer;
//
//    private String reviewPeriod; // e.g., Q1-2025, June 2025
//    private LocalDateTime reviewDate;
//
//    @Column(columnDefinition = "TEXT")
//    private String scores; // JSON string like {"communication":4,"teamwork":5,"technical":3}
//
//    private Double overallRating;
//    private String comments;
//    private String reviewType; // Monthly or Quarterly
//    private String pdfUrl;
//
//    // Getters and Setters
//
//    public Long getReviewId() {
//        return reviewId;
//    }
//
//    public void setReviewId(Long reviewId) {
//        this.reviewId = reviewId;
//    }
//
//    public Employee getEmployee() {
//        return employee;
//    }
//
//    public void setEmployee(Employee employee) {
//        this.employee = employee;
//    }
//
//    public Employee getReviewer() {
//        return reviewer;
//    }
//
//    public void setReviewer(Employee reviewer) {
//        this.reviewer = reviewer;
//    }
//
//    public String getReviewPeriod() {
//        return reviewPeriod;
//    }
//
//    public void setReviewPeriod(String reviewPeriod) {
//        this.reviewPeriod = reviewPeriod;
//    }
//
//    public LocalDateTime getReviewDate() {
//        return reviewDate;
//    }
//
//    public void setReviewDate(LocalDateTime reviewDate) {
//        this.reviewDate = reviewDate;
//    }
//
//    public String getScores() {
//        return scores;
//    }
//
//    public void setScores(String scores) {
//        this.scores = scores;
//    }
//
//    public Double getOverallRating() {
//        return overallRating;
//    }
//
//    public void setOverallRating(Double overallRating) {
//        this.overallRating = overallRating;
//    }
//
//    public String getComments() {
//        return comments;
//    }
//
//    public void setComments(String comments) {
//        this.comments = comments;
//    }
//
//    public String getReviewType() {
//        return reviewType;
//    }
//
//    public void setReviewType(String reviewType) {
//        this.reviewType = reviewType;
//    }
//
//    public String getPdfUrl() {
//        return pdfUrl;
//    }
//
//    public void setPdfUrl(String pdfUrl) {
//        this.pdfUrl = pdfUrl;
//    }
//}

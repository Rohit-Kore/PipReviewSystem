package com.example.PipReviewSystem.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class PerformanceReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "reviewer_id")
    private Employee reviewer;

    private String reviewPeriod; // Q1-2025, June 2025
    private LocalDateTime reviewDate;

    @Column(columnDefinition = "TEXT")
    private String scores; // JSON: {"communication":4,"teamwork":5,"technical":3}

    private Double overallRating;
    private String comments;
    private String reviewType; // Monthly or Quarterly
    private String pdfUrl;
}

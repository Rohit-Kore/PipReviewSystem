package com.example.PipReviewSystem.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Employee createdBy;

    private String reportType; // PERFORMANCE, PIP, FEEDBACK
    private LocalDateTime generatedOn;

    // private String fileUrl; // Uncomment if file URL support is added
}

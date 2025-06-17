package com.example.PipReviewSystem.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class PIP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pipId;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String goals;
    private String progress;
    private String status; // ACTIVE, COMPLETED, FAILED

    @ManyToOne
    @JoinColumn(name = "reviewer_id")
    private Employee reviewer;

    private String outcome;
    private String comments;
}


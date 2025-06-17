package com.example.PipReviewSystem.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Employee user;

    private String action; // CREATE, UPDATE, DELETE
    private String entity; // PIP, FEEDBACK, REVIEW
    private Long entityId;
    private LocalDateTime timestamp;
    private String remarks;
}

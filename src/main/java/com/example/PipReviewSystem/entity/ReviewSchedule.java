package com.example.PipReviewSystem.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ReviewSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    private LocalDateTime reviewDate;

    // reminder flags to avoid duplicate sends
    private Boolean reminder3DaysSent = false;
    private Boolean reminder1DaySent = false;

    // who scheduled (email)
    private String scheduledBy;
}


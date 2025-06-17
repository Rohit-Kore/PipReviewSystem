package com.example.PipReviewSystem.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Employee employee;

    private String title;
    private String message;
    private String type; // REMINDER, ALERT, INFO
    private Boolean isRead = false;
    private LocalDateTime timestamp;
}

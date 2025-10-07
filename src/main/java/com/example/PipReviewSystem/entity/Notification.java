package com.example.PipReviewSystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    // This annotation tells Jackson to ignore this side of the relationship
    // during serialization to prevent an infinite loop.
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Employee employee;

    private String title;
    private String message;
    private String type; // REMINDER, ALERT, INFO
    private Boolean isRead = false;
    private LocalDateTime timestamp;
}

package com.example.PipReviewSystem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Entity
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackId;

    @ManyToOne
    @JoinColumn(name = "from_user_id")
    private Employee fromUser;

    @ManyToOne
    @JoinColumn(name = "to_user_id")
    private Employee toUser;

    private String feedbackType; // PEER, SELF, MANAGER
    private String comments;
    private int rating;
    private Boolean isAnonymous;
    private LocalDateTime createdDate;
}

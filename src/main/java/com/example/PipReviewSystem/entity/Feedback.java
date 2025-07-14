package com.example.PipReviewSystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Setter
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

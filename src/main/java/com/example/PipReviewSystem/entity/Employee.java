package com.example.PipReviewSystem.entity;

import com.example.PipReviewSystem.enums.Role;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Employee {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID employeeId;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; //  using ENUM

    private String department;
    private String designation;
    private String skills;
    private String currentKRA;
    private String kpi;

    private UUID managerId; // UUID instead of Long for consistency

    private String photoUrl;
    private LocalDateTime joiningDate;

    private String status; // ACTIVE, INACTIVE, UNDER_PIP

    private String otp;
    private LocalDateTime otpGeneratedTime;

    private boolean isTemporaryPassword;
    private LocalDateTime temporaryPasswordGeneratedTime;

    private String passwordResetToken;
    private LocalDateTime passwordResetTokenExpiryTime;

    // Relationships
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<PIP> pips;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<PerformanceReview> performanceReviews;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Notification> notifications;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<SkillGapAnalysis> skillGapAnalyses;

    @OneToMany(mappedBy = "toUser", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Feedback> feedbacksReceived;

    @OneToMany(mappedBy = "fromUser", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Feedback> feedbacksGiven;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    @JsonManagedReference
    private List<AuditLog> auditLogs;


}

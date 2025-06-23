package com.example.PipReviewSystem.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;

    private String name;
    private String email;
    private String password;
    private String role; // EMPLOYEE, MANAGER, ADMIN, HR
    private String department;
    private String designation;
    private String skills;
    private String currentKRA;
    private String kpi;
    private Long managerId; // Self-reference for reporting
    private String photoUrl;
    private LocalDateTime joiningDate;
    private String status; // ACTIVE, INACTIVE, UNDER_PIP

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<PIP> pips;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<PerformanceReview> performanceReviews;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Notification> notifications;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<SkillGapAnalysis> skillGapAnalyses;

    @OneToMany(mappedBy = "toUser", cascade = CascadeType.ALL)
    private List<Feedback> feedbacksReceived;

    @OneToMany(mappedBy = "fromUser", cascade = CascadeType.ALL)
    private List<Feedback> feedbacksGiven;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<AuditLog> auditLogs;
}

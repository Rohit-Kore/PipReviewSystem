package com.example.PipReviewSystem.entity;

import com.example.PipReviewSystem.enums.Role;

import com.fasterxml.jackson.annotation.*;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;

import java.util.List;

import java.util.UUID;

@NoArgsConstructor

@AllArgsConstructor

@Getter

@Setter

@Entity


@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@JsonIdentityInfo(

        generator = ObjectIdGenerators.PropertyGenerator.class,

        property = "employeeId"

)


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

    private Role role;

    private String department;

    private String designation;

    private String skills;

    private String currentKRA;

    private String kpi;

    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "manager_id")

    @JsonIdentityReference(alwaysAsId = true)

    private Employee manager;

    @Column(name = "manager_id", insertable = false, updatable = false)


    private UUID managerId;

    private String photoUrl;

    private LocalDateTime joiningDate;

    private String status;

    private String otp;

    private LocalDateTime otpGeneratedTime;

    private boolean isTemporaryPassword;

    private LocalDateTime temporaryPasswordGeneratedTime;

    private String passwordResetToken;

    private LocalDateTime passwordResetTokenExpiryTime;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)

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

    private List<AuditLog> auditLogs;

}


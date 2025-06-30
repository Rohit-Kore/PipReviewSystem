package com.example.PipReviewSystem.entity;

import com.example.PipReviewSystem.enums.Role;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

//@Getter
//@Setter
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

    // Relationships
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
    @JsonManagedReference
    private List<AuditLog> auditLogs;


    public UUID getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(UUID employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getCurrentKRA() {
        return currentKRA;
    }

    public void setCurrentKRA(String currentKRA) {
        this.currentKRA = currentKRA;
    }

    public String getKpi() {
        return kpi;
    }

    public void setKpi(String kpi) {
        this.kpi = kpi;
    }

    public UUID getManagerId() {
        return managerId;
    }

    public void setManagerId(UUID managerId) {
        this.managerId = managerId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public LocalDateTime getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDateTime joiningDate) {
        this.joiningDate = joiningDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<PIP> getPips() {
        return pips;
    }

    public void setPips(List<PIP> pips) {
        this.pips = pips;
    }

    public List<PerformanceReview> getPerformanceReviews() {
        return performanceReviews;
    }

    public void setPerformanceReviews(List<PerformanceReview> performanceReviews) {
        this.performanceReviews = performanceReviews;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public List<SkillGapAnalysis> getSkillGapAnalyses() {
        return skillGapAnalyses;
    }

    public void setSkillGapAnalyses(List<SkillGapAnalysis> skillGapAnalyses) {
        this.skillGapAnalyses = skillGapAnalyses;
    }

    public List<Feedback> getFeedbacksReceived() {
        return feedbacksReceived;
    }

    public void setFeedbacksReceived(List<Feedback> feedbacksReceived) {
        this.feedbacksReceived = feedbacksReceived;
    }

    public List<Feedback> getFeedbacksGiven() {
        return feedbacksGiven;
    }

    public void setFeedbacksGiven(List<Feedback> feedbacksGiven) {
        this.feedbacksGiven = feedbacksGiven;
    }

    public List<AuditLog> getAuditLogs() {
        return auditLogs;
    }

    public void setAuditLogs(List<AuditLog> auditLogs) {
        this.auditLogs = auditLogs;
    }
}

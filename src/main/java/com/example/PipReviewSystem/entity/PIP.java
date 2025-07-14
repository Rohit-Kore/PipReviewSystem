package com.example.PipReviewSystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.*;

import java.time.LocalDateTime;

@Entity
//@Getter
//@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PIP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pipId;

    // Employee for whom PIP is created
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    @JsonBackReference(value = "employee-pip")
    private Employee employee;

    // Reviewer or Manager creating the PIP
    @ManyToOne
    @JoinColumn(name = "reviewer_id", nullable = false)
    @JsonBackReference(value = "reviewer-pip")
    private Employee reviewer;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private String goals;
    private String progress;

    private String status; // e.g., ACTIVE, COMPLETED, FAILED
    private String outcome;
    private String comments;


    public Long getPipId() {
        return pipId;
    }

    public void setPipId(Long pipId) {
        this.pipId = pipId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Employee getReviewer() {
        return reviewer;
    }

    public void setReviewer(Employee reviewer) {
        this.reviewer = reviewer;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
























//old one
//public class PIP {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long pipId;
//
//    @ManyToOne
//    @JoinColumn(name = "employee_id")
//    private Employee employee;
//
//    private LocalDateTime startDate;
//    private LocalDateTime endDate;
//    private String goals;
//    private String progress;
//    private String status; // ACTIVE, COMPLETED, FAILED
//
//    @ManyToOne
//    @JoinColumn(name = "reviewer_id")
//    private Employee reviewer;
//
//    private String outcome;
//    private String comments;
//}




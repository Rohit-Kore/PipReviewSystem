package com.example.PipReviewSystem.entity;

import com.fasterxml.jackson.annotation.*;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;

@Entity

@NoArgsConstructor

@AllArgsConstructor

//@Getter
//
//@Setter

public class PIP {

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long pipId;

    @ManyToOne(fetch = FetchType.EAGER)

    @JoinColumn(name = "employee_id", nullable = false)

    @JsonIdentityReference(alwaysAsId = false)

    private Employee employee;

    @ManyToOne(fetch = FetchType.EAGER)

    @JoinColumn(name = "reviewer_id", nullable = false)

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "manager", "pips", "performanceReviews"})

    private Employee reviewer;



    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String goals;

    private String progress;

    private String status;

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
    public void setLastProgressReviewDate(LocalDateTime now) {
    }
}


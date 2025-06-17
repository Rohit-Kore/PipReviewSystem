package com.example.PipReviewSystem.entity;

import jakarta.persistence.*;

@Entity
public class SkillGapAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long analysisId;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private String skill;
    private int requiredLevel;
    private int currentLevel;
    private int gapLevel;
    private String suggestedTraining;
}

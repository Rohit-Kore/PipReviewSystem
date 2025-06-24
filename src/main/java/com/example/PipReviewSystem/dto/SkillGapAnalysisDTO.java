package com.example.PipReviewSystem.dto;

import lombok.Data;

@Data
public class SkillGapAnalysisDTO {
    private Long employeeId;
    private String skill;
    private int requiredLevel;
    private int currentLevel;
    private int gapLevel;
    private String suggestedTraining;
}

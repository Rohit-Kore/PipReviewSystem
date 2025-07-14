package com.example.PipReviewSystem.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class SkillGapAnalysisDTO {
    private UUID employeeId;
    private String skill;
    private int requiredLevel;
    private int currentLevel;
    private int gapLevel;
    private String suggestedTraining;
}

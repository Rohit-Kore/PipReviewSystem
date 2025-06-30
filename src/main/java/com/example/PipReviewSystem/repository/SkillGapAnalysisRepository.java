package com.example.PipReviewSystem.repository;

import com.example.PipReviewSystem.entity.SkillGapAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SkillGapAnalysisRepository extends JpaRepository<SkillGapAnalysis, Long> {
    List<SkillGapAnalysis> findByEmployeeEmployeeId(UUID employeeId);
    List<SkillGapAnalysis> findBySkillContainingIgnoreCase(String skill);
}

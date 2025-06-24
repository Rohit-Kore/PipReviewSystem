package com.example.PipReviewSystem.service;

import com.example.PipReviewSystem.dto.SkillGapAnalysisDTO;
import com.example.PipReviewSystem.entity.Employee;
import com.example.PipReviewSystem.entity.SkillGapAnalysis;
import com.example.PipReviewSystem.repository.EmployeeRepository;
import com.example.PipReviewSystem.repository.SkillGapAnalysisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillGapAnalysisService {

    private final SkillGapAnalysisRepository skillRepo;
    private final EmployeeRepository employeeRepo;

    public SkillGapAnalysis createSkillGap(SkillGapAnalysisDTO dto) {
        Employee employee = employeeRepo.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        SkillGapAnalysis gap = new SkillGapAnalysis();
        gap.setEmployee(employee);
        gap.setSkill(dto.getSkill());
        gap.setRequiredLevel(dto.getRequiredLevel());
        gap.setCurrentLevel(dto.getCurrentLevel());
        gap.setGapLevel(dto.getGapLevel());
        gap.setSuggestedTraining(dto.getSuggestedTraining());

        return skillRepo.save(gap);
    }

    public SkillGapAnalysis getById(Long id) {
        return skillRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill gap analysis not found with ID: " + id));
    }

    public List<SkillGapAnalysis> getAll() {
        return skillRepo.findAll();
    }

    public List<SkillGapAnalysis> getByEmployee(Long empId) {
        return skillRepo.findByEmployeeEmployeeId(empId);
    }

    public SkillGapAnalysis updateSkillGap(Long id, SkillGapAnalysisDTO dto) {
        SkillGapAnalysis gap = getById(id);

        gap.setSkill(dto.getSkill());
        gap.setRequiredLevel(dto.getRequiredLevel());
        gap.setCurrentLevel(dto.getCurrentLevel());
        gap.setGapLevel(dto.getGapLevel());
        gap.setSuggestedTraining(dto.getSuggestedTraining());

        if (dto.getEmployeeId() != null) {
            Employee employee = employeeRepo.findById(dto.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            gap.setEmployee(employee);
        }

        return skillRepo.save(gap);
    }

    public void deleteSkillGap(Long id) {
        if (!skillRepo.existsById(id)) {
            throw new RuntimeException("Skill gap analysis not found with ID: " + id);
        }
        skillRepo.deleteById(id);
    }
}

package com.example.PipReviewSystem.service;

import com.example.PipReviewSystem.dto.SkillGapAnalysisDTO;
import com.example.PipReviewSystem.entity.Employee;
import com.example.PipReviewSystem.entity.SkillGapAnalysis;
import com.example.PipReviewSystem.repository.EmployeeRepository;
import com.example.PipReviewSystem.repository.SkillGapAnalysisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SkillGapAnalysisService {

    @Autowired
    private SkillGapAnalysisRepository skillRepo;

    @Autowired
    private EmployeeRepository employeeRepo;

    @Autowired
    private NotificationService notificationService; // Notification service inject

    // Create SkillGap and send notification
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

        SkillGapAnalysis savedGap = skillRepo.save(gap);

        // Send browser notification to employee
        notificationService.createNotification(
                employee,
                "New Skill Gap Analysis",
                "A new skill gap analysis has been assigned to you, " + employee.getName() + ": " + dto.getSkill(),
                "INFO"
        );
        // Send notification to manager if exists, with employee name
        if (employee.getManager() != null) {
            notificationService.createNotification(
                employee.getManager(),
                "New Skill Gap Analysis for Team Member",
                "A new skill gap analysis has been assigned to your team member: " + employee.getName() + " for skill: " + dto.getSkill(),
                "INFO"
            );
        }
        return savedGap;
    }

    // Update SkillGap and send notification
    public SkillGapAnalysis updateSkillGap(Long id, SkillGapAnalysisDTO dto) {
        SkillGapAnalysis gap = skillRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill gap analysis not found"));

        gap.setSkill(dto.getSkill());
        gap.setRequiredLevel(dto.getRequiredLevel());
        gap.setCurrentLevel(dto.getCurrentLevel());
        gap.setGapLevel(dto.getGapLevel());
        gap.setSuggestedTraining(dto.getSuggestedTraining());

        if (dto.getEmployeeId() != null) {
            Employee employee = employeeRepo.findById(dto.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            gap.setEmployee(employee);

            notificationService.createNotification(
                    employee,
                    "Skill Gap Analysis Updated",
                    "Your skill gap analysis for " + dto.getSkill() + " has been updated.",
                    "INFO"
            );
            // Notify manager if exists, with employee name
            if (employee.getManager() != null) {
                notificationService.createNotification(
                    employee.getManager(),
                    "Skill Gap Analysis Updated for Team Member",
                    "Skill gap analysis for your team member " + employee.getName() + " has been updated for skill: " + dto.getSkill(),
                    "INFO"
                );
            }
        }
        return skillRepo.save(gap);
    }

    public SkillGapAnalysis getById(Long id) {
        return skillRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill gap analysis not found with ID: " + id));
    }

    public List<SkillGapAnalysis> getAll() {
        return skillRepo.findAll();
    }

    public List<SkillGapAnalysis> getByEmployee(UUID empId) {
        return skillRepo.findByEmployeeEmployeeId(empId);
    }

    public void deleteSkillGap(Long id) {
        SkillGapAnalysis gap = skillRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill gap analysis not found with ID: " + id));

        Employee employee = gap.getEmployee();
        skillRepo.deleteById(id);

        notificationService.createNotification(
                employee,
                "Skill Gap Analysis Deleted",
                "Your skill gap analysis for " + gap.getSkill() + " has been deleted.",
                "ALERT"
        );
        // Notify manager if exists, with employee name
        if (employee.getManager() != null) {
            notificationService.createNotification(
                employee.getManager(),
                "Skill Gap Analysis Deleted for Team Member",
                "Skill gap analysis for your team member " + employee.getName() + " has been deleted for skill: " + gap.getSkill(),
                "ALERT"
            );
        }
    }
}

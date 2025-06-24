package com.example.PipReviewSystem.controller;

import com.example.PipReviewSystem.dto.SkillGapAnalysisDTO;
import com.example.PipReviewSystem.entity.SkillGapAnalysis;
import com.example.PipReviewSystem.service.SkillGapAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skill-gap")
@RequiredArgsConstructor
public class SkillGapAnalysisController {

    private final SkillGapAnalysisService skillService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody SkillGapAnalysisDTO dto) {
        try {
            SkillGapAnalysis saved = skillService.createSkillGap(dto);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Failed to create skill gap: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.ok(skillService.getAll());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching skill gaps");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(skillService.getById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Not found: " + e.getMessage());
        }
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getByEmployee(@PathVariable Long employeeId) {
        try {
            return ResponseEntity.ok(skillService.getByEmployee(employeeId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching employee's skill gaps: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody SkillGapAnalysisDTO dto) {
        try {
            return ResponseEntity.ok(skillService.updateSkillGap(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Update failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            skillService.deleteSkillGap(id);
            return ResponseEntity.ok("Skill gap deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Delete failed: " + e.getMessage());
        }
    }
}

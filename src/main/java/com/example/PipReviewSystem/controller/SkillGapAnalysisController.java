package com.example.PipReviewSystem.controller;

import com.example.PipReviewSystem.dto.SkillGapAnalysisDTO;
import com.example.PipReviewSystem.entity.SkillGapAnalysis;
import com.example.PipReviewSystem.service.SkillGapAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/skill-gap")
@RequiredArgsConstructor

public class SkillGapAnalysisController {
@Autowired
    private  SkillGapAnalysisService service;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('HR', 'MANAGER')")
    public ResponseEntity<?> createSkillGap(@RequestBody SkillGapAnalysisDTO dto) {
        try {
            SkillGapAnalysis created = service.createSkillGap(dto);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error creating skill gap: " + e.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('HR', 'MANAGER', 'ADMIN')")
    public ResponseEntity<List<SkillGapAnalysis>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('HR', 'MANAGER', 'ADMIN')")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.getById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Not found: " + e.getMessage());
        }
    }

    @GetMapping("/employee/{empId}")
    @PreAuthorize("hasAnyAuthority('EMPLOYEE', 'HR', 'MANAGER')")
    public ResponseEntity<?> getByEmployee(@PathVariable UUID empId) {
        try {
            return ResponseEntity.ok(service.getByEmployee(empId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('HR', 'MANAGER')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody SkillGapAnalysisDTO dto) {
        try {
            return ResponseEntity.ok(service.updateSkillGap(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Update failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            service.deleteSkillGap(id);
            return ResponseEntity.ok("Deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Delete failed: " + e.getMessage());
        }
    }
}

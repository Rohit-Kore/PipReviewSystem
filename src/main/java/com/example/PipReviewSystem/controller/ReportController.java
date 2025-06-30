package com.example.PipReviewSystem.controller;

import com.example.PipReviewSystem.dto.ReportResponseDTO;
import com.example.PipReviewSystem.entity.Report;
import com.example.PipReviewSystem.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping
    public Report createReport(@RequestBody Report report, Principal principal) {
        return reportService.createReport(report, principal.getName());
    }

    @GetMapping
    public List<ReportResponseDTO> getAllReports() {
        return reportService.getAllReports().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ReportResponseDTO getReportById(@PathVariable Long id) {
        Report report = reportService.getReportById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + id));
        return mapToDto(report);
    }

    @GetMapping("/employee/{employeeId}")
    public List<ReportResponseDTO> getReportsByEmployee(@PathVariable UUID employeeId) {
        return reportService.getReportsByEmployeeId(employeeId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/type/{reportType}")
    public List<ReportResponseDTO> getReportsByType(@PathVariable String reportType) {
        return reportService.getReportsByType(reportType).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public Report updateReport(@PathVariable Long id, @RequestBody Report report) {
        return reportService.updateReport(id, report);
    }

    @DeleteMapping("/{id}")
    public String deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return "Report with ID " + id + " deleted successfully.";
    }

    private ReportResponseDTO mapToDto(Report report) {
        ReportResponseDTO dto = new ReportResponseDTO();
        dto.setReportId(report.getReportId());
        dto.setReportType(report.getReportType());
        dto.setFileUrl(report.getFileUrl());
        dto.setGeneratedOn(report.getGeneratedOn());
        dto.setCreatedById(report.getCreatedBy().getEmployeeId());
        dto.setCreatedByName(report.getCreatedBy().getName());
        dto.setCreatedByEmail(report.getCreatedBy().getEmail());
        return dto;
    }
}

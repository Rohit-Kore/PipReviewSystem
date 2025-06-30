package com.example.PipReviewSystem.service;

import com.example.PipReviewSystem.entity.Report;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReportService {
    Report createReport(Report report, String createdByEmail);
    List<Report> getAllReports();
    Optional<Report> getReportById(Long id);
    List<Report> getReportsByEmployeeId(UUID employeeId);
    List<Report> getReportsByType(String reportType);
    Report updateReport(Long id, Report updatedReport);
    void deleteReport(Long id);
}

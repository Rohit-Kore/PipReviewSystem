package com.example.PipReviewSystem.service;

import com.example.PipReviewSystem.entity.Employee;
import com.example.PipReviewSystem.entity.Report;
import com.example.PipReviewSystem.repository.EmployeeRepository;
import com.example.PipReviewSystem.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Report createReport(Report report, String createdByEmail) {
        // Fetch the logged-in employee
        Employee creator = employeeRepository.findByEmail(createdByEmail)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        report.setCreatedBy(creator);
        report.setGeneratedOn(java.time.LocalDateTime.now());
        return reportRepository.save(report);
    }

    @Override
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    @Override
    public Optional<Report> getReportById(Long id) {
        return reportRepository.findById(id);
    }

    @Override
    public List<Report> getReportsByEmployeeId(UUID employeeId) {
        return reportRepository.findByCreatedByEmployeeId(employeeId);
    }

    @Override
    public List<Report> getReportsByType(String reportType) {
        return reportRepository.findByReportType(reportType);
    }

    @Override
    public Report updateReport(Long id, Report updatedReport) {
        return reportRepository.findById(id).map(report -> {
            report.setReportType(updatedReport.getReportType());
            report.setCreatedBy(updatedReport.getCreatedBy());
            report.setGeneratedOn(updatedReport.getGeneratedOn());
            report.setFileUrl(updatedReport.getFileUrl());
            return reportRepository.save(report);
        }).orElseThrow(() -> new RuntimeException("Report not found with ID: " + id));
    }

    @Override
    public void deleteReport(Long id) {
        if (!reportRepository.existsById(id)) {
            throw new RuntimeException("Report not found with ID: " + id);
        }
        reportRepository.deleteById(id);
    }
}

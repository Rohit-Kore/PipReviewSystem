package com.example.PipReviewSystem.Service;

import com.example.PipReviewSystem.entity.Report;
import com.example.PipReviewSystem.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    public Report createReport(Report report) {
        report.setGeneratedOn(java.time.LocalDateTime.now());
        return reportRepository.save(report);
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public Optional<Report> getReportById(Long id) {
        return reportRepository.findById(id);
    }

    public List<Report> getReportsByEmployeeId(Long employeeId) {
        return reportRepository.findByCreatedByEmployeeId(employeeId);
    }

    public List<Report> getReportsByType(String reportType) {
        return reportRepository.findByReportType(reportType);
    }

    public Report updateReport(Long id, Report updatedReport) {
        return reportRepository.findById(id).map(report -> {
            report.setReportType(updatedReport.getReportType());
            report.setCreatedBy(updatedReport.getCreatedBy());
            report.setGeneratedOn(updatedReport.getGeneratedOn());
            report.setFileUrl(updatedReport.getFileUrl());
            return reportRepository.save(report);
        }).orElseThrow(() -> new RuntimeException("Report not found with ID: " + id));
    }

    public void deleteReport(Long id) {
        if (!reportRepository.existsById(id)) {
            throw new RuntimeException("Report not found with ID: " + id);
        }
        reportRepository.deleteById(id);
    }
}

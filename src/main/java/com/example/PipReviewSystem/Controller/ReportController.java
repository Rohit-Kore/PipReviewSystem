package com.example.PipReviewSystem.Controller;

import com.example.PipReviewSystem.entity.Report;
import com.example.PipReviewSystem.Service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping
    public Report createReport(@RequestBody Report report) {
        return reportService.createReport(report);
    }

    @GetMapping
    public List<Report> getAllReports() {
        return reportService.getAllReports();
    }

    @GetMapping("/{id}")
    public Report getReportById(@PathVariable Long id) {
        return reportService.getReportById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + id));
    }

    @GetMapping("/employee/{employeeId}")
    public List<Report> getReportsByEmployee(@PathVariable Long employeeId) {
        return reportService.getReportsByEmployeeId(employeeId);
    }

    @GetMapping("/type/{reportType}")
    public List<Report> getReportsByType(@PathVariable String reportType) {
        return reportService.getReportsByType(reportType);
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
}

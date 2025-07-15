package com.example.PipReviewSystem.controller;

import com.example.PipReviewSystem.dto.ReportRequestDTO;
import com.example.PipReviewSystem.dto.ReportResponseDTO;
import com.example.PipReviewSystem.dto.ReportUpdateDTO;
import com.example.PipReviewSystem.entity.Report;
import com.example.PipReviewSystem.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;



@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired private ReportService reportService;

    @PreAuthorize("hasAuthority('MANAGER')")
    @PostMapping
    public ResponseEntity<?> createReport(@RequestBody ReportRequestDTO dto, Principal principal) {
        try {
            return ResponseEntity.ok(reportService.createReport(dto, principal.getName()));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'HR', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getReportById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(reportService.getReportById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'HR', 'ADMIN')")
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<ReportResponseDTO>> getReportsForEmployee(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(reportService.getReportsForEmployee(employeeId));
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    @GetMapping("/manager/{managerId}")
    public ResponseEntity<List<ReportResponseDTO>> getReportsByManager(@PathVariable UUID managerId) {
        return ResponseEntity.ok(reportService.getReportsByManager(managerId));
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateReport(@PathVariable Long id, @RequestBody ReportUpdateDTO dto) {
        try {
            return ResponseEntity.ok(reportService.updateReport(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReport(@PathVariable Long id) {
        try {
            reportService.deleteReport(id);
            return ResponseEntity.ok("Report deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

//
//@RestController
//@RequestMapping("/api/reports")
//public class ReportController {
//
//    @Autowired
//    private ReportService reportService;
//
//    // Only HR and ADMIN can create a report
//    @PreAuthorize("hasAnyAuthority('HR', 'ADMIN')")
//    @PostMapping
//    public Report createReport(@RequestBody Report report, Principal principal) {
//        return reportService.createReport(report, principal.getName());
//    }
//
//    // HR and MANAGER can view all reports
//    @PreAuthorize("hasAnyAuthority('HR', 'MANAGER','ADMIN')")             //admin
//    @GetMapping
//    public List<ReportResponseDTO> getAllReports() {
//        return reportService.getAllReports().stream()
//                .map(this::mapToDto)
//                .collect(Collectors.toList());
//    }
//
//    // HR and ADMIN can view a specific report by ID
//    @PreAuthorize("hasAnyAuthority('HR', 'ADMIN')")
//    @GetMapping("/{id}")
//    public ReportResponseDTO getReportById(@PathVariable Long id) {
//        Report report = reportService.getReportById(id)
//                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + id));
//        return mapToDto(report);
//    }
//
//    // HR, MANAGER, EMPLOYEE can fetch reports by employee ID
//    @PreAuthorize("hasAnyAuthority('HR', 'MANAGER', 'EMPLOYEE','ADMIN')")          //ADMIN
//    @GetMapping("/employee/{employeeId}")
//    public List<ReportResponseDTO> getReportsByEmployee(@PathVariable UUID employeeId) {
//        return reportService.getReportsByEmployeeId(employeeId).stream()
//                .map(this::mapToDto)
//                .collect(Collectors.toList());
//    }
//
//    // HR and ADMIN can filter reports by type
//    @PreAuthorize("hasAnyAuthority('HR', 'ADMIN')")
//    @GetMapping("/type/{reportType}")
//    public List<ReportResponseDTO> getReportsByType(@PathVariable String reportType) {
//        return reportService.getReportsByType(reportType).stream()
//                .map(this::mapToDto)
//                .collect(Collectors.toList());
//    }
//
//    // HR and ADMIN can update a report
//    @PreAuthorize("hasAnyAuthority('HR', 'ADMIN')")
//    @PutMapping("/{id}")
//    public Report updateReport(@PathVariable Long id, @RequestBody Report report) {
//        return reportService.updateReport(id, report);
//    }
//
//    // HR and ADMIN can delete a report
//    @PreAuthorize("hasAnyAuthority('HR', 'ADMIN')")
//    @DeleteMapping("/{id}")
//    public String deleteReport(@PathVariable Long id) {
//        reportService.deleteReport(id);
//        return "Report with ID " + id + " deleted successfully.";
//    }
//
//    private ReportResponseDTO mapToDto(Report report) {
//        ReportResponseDTO dto = new ReportResponseDTO();
//        dto.setReportId(report.getReportId());
//        dto.setReportType(report.getReportType());
//        dto.setFileUrl(report.getFileUrl());
//        dto.setGeneratedOn(report.getGeneratedOn());
//        dto.setCreatedById(report.getCreatedBy().getEmployeeId());
//        dto.setCreatedByName(report.getCreatedBy().getName());
//        dto.setCreatedByEmail(report.getCreatedBy().getEmail());
//        return dto;
//    }
//}

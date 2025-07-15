package com.example.PipReviewSystem.service;

import com.example.PipReviewSystem.dto.ReportRequestDTO;
import com.example.PipReviewSystem.dto.ReportResponseDTO;
import com.example.PipReviewSystem.dto.ReportUpdateDTO;
import com.example.PipReviewSystem.entity.Employee;
import com.example.PipReviewSystem.entity.Report;
import com.example.PipReviewSystem.repository.EmployeeRepository;
import com.example.PipReviewSystem.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportRepository reportRepo;

    @Autowired
    private EmployeeRepository employeeRepo;

    private ReportResponseDTO mapToDto(Report r) {
        ReportResponseDTO dto = new ReportResponseDTO();
        dto.setReportId(r.getReportId());
        dto.setReportType(r.getReportType());
        dto.setFileUrl(r.getFileUrl());
        dto.setGeneratedOn(r.getGeneratedOn());
        dto.setCreatedById(r.getCreatedBy().getEmployeeId());
        dto.setCreatedByName(r.getCreatedBy().getName());
        dto.setTargetEmployeeId(r.getTargetEmployee().getEmployeeId());
        dto.setTargetEmployeeName(r.getTargetEmployee().getName());
        return dto;
    }

    @Override
    public ReportResponseDTO createReport(ReportRequestDTO dto, String managerEmail) {
        Employee creator = employeeRepo.findByEmail(managerEmail)
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        if (!creator.getRole().name().equalsIgnoreCase("MANAGER"))

            throw new RuntimeException("Only MANAGER can create reports");

        Employee target = employeeRepo.findByEmployeeId(dto.getTargetEmployeeId())
                .orElseThrow(() -> new RuntimeException("Target employee not found"));

        Report report = new Report();
        report.setCreatedBy(creator);
        report.setTargetEmployee(target);
        report.setReportType(dto.getReportType());
        report.setFileUrl(dto.getFileUrl());
        report.setGeneratedOn(LocalDateTime.now());
        System.out.println("DB Creator role = " + creator.getRole());
        System.out.println("PRINCIPAL NAME = " + managerEmail);
        System.out.println("DB Creator email = " + creator.getEmail());

        return mapToDto(reportRepo.save(report));
    }



    @Override
    public ReportResponseDTO updateReport(Long id, ReportUpdateDTO dto) {
        Report report = reportRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        if (dto.getReportType() != null) report.setReportType(dto.getReportType());
        if (dto.getFileUrl() != null) report.setFileUrl(dto.getFileUrl());

        return mapToDto(reportRepo.save(report));
    }

    @Override
    public List<ReportResponseDTO> getReportsForEmployee(UUID employeeId) {
        return reportRepo.findByTargetEmployeeEmployeeId(employeeId)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<ReportResponseDTO> getReportsByManager(UUID managerId) {
        return reportRepo.findByCreatedByEmployeeId(managerId)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public ReportResponseDTO getReportById(Long id) {
        return reportRepo.findById(id).map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Report not found"));
    }

    @Override
    public void deleteReport(Long id) {
        if (!reportRepo.existsById(id)) {
            throw new RuntimeException("Report not found");
        }
        reportRepo.deleteById(id);
    }
}












































//@Service
//public class ReportServiceImpl implements ReportService {
//
//    @Autowired
//    private ReportRepository reportRepository;
//
//    @Autowired
//    private EmployeeRepository employeeRepository;
//
//    @Override
//    public Report createReport(Report report, String createdByEmail) {
//        // Fetch the logged-in employee
//        Employee creator = employeeRepository.findByEmail(createdByEmail)
//                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
//
//        report.setCreatedBy(creator);
//        report.setGeneratedOn(java.time.LocalDateTime.now());
//        return reportRepository.save(report);
//    }
//
//    @Override
//    public List<Report> getAllReports() {
//        return reportRepository.findAll();
//    }
//
//    @Override
//    public Optional<Report> getReportById(Long id) {
//        return reportRepository.findById(id);
//    }
//
//    @Override
//    public List<Report> getReportsByEmployeeId(UUID employeeId) {
//        return reportRepository.findByCreatedByEmployeeId(employeeId);
//    }
//
//    @Override
//    public List<Report> getReportsByType(String reportType) {
//        return reportRepository.findByReportType(reportType);
//    }
//
//    @Override
//    public Report updateReport(Long id, Report updatedReport) {
//        return reportRepository.findById(id).map(report -> {
//            report.setReportType(updatedReport.getReportType());
//            report.setCreatedBy(updatedReport.getCreatedBy());
//            report.setGeneratedOn(updatedReport.getGeneratedOn());
//            report.setFileUrl(updatedReport.getFileUrl());
//            return reportRepository.save(report);
//        }).orElseThrow(() -> new RuntimeException("Report not found with ID: " + id));
//    }
//
//    @Override
//    public void deleteReport(Long id) {
//        if (!reportRepository.existsById(id)) {
//            throw new RuntimeException("Report not found with ID: " + id);
//        }
//        reportRepository.deleteById(id);
//    }
//}

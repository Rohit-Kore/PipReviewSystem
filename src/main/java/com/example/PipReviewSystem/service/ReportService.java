package com.example.PipReviewSystem.service;

import com.example.PipReviewSystem.dto.ReportRequestDTO;
import com.example.PipReviewSystem.dto.ReportResponseDTO;
import com.example.PipReviewSystem.dto.ReportUpdateDTO;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public interface ReportService {
    ReportResponseDTO createReport(ReportRequestDTO dto, String managerEmail);
    ReportResponseDTO updateReport(Long id, ReportUpdateDTO dto);
    List<ReportResponseDTO> getReportsForEmployee(UUID employeeId);
    List<ReportResponseDTO> getReportsByManager(UUID managerId);
    ReportResponseDTO getReportById(Long id);
    void deleteReport(Long id);
    List<ReportResponseDTO> getAllReports();

    // 👇 New method for downloading
    Path getReportFilePath(Long id);
}


















//package com.example.PipReviewSystem.service;
//
//import com.example.PipReviewSystem.dto.ReportRequestDTO;
//import com.example.PipReviewSystem.dto.ReportResponseDTO;
//import com.example.PipReviewSystem.dto.ReportUpdateDTO;
//import com.example.PipReviewSystem.entity.Report;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//
//public interface ReportService {
//    ReportResponseDTO createReport(ReportRequestDTO dto, String managerEmail);
//    ReportResponseDTO updateReport(Long id, ReportUpdateDTO dto);
//    List<ReportResponseDTO> getReportsForEmployee(UUID employeeId);
//    List<ReportResponseDTO> getReportsByManager(UUID managerId);
//    ReportResponseDTO getReportById(Long id);
//    void deleteReport(Long id);
//
//    List<ReportResponseDTO> getAllReports();
//
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
////public interface ReportService {
////    Report createReport(Report report, String createdByEmail);
////    List<Report> getAllReports();
////    Optional<Report> getReportById(Long id);
////    List<Report> getReportsByEmployeeId(UUID employeeId);
////    List<Report> getReportsByType(String reportType);
////    Report updateReport(Long id, Report updatedReport);
////    void deleteReport(Long id);
////}

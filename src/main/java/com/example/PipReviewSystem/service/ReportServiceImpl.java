package com.example.PipReviewSystem.service;

import com.example.PipReviewSystem.dto.ReportRequestDTO;
import com.example.PipReviewSystem.dto.ReportResponseDTO;
import com.example.PipReviewSystem.dto.ReportUpdateDTO;
import com.example.PipReviewSystem.entity.Employee;
import com.example.PipReviewSystem.entity.Report;
import com.example.PipReviewSystem.enums.Role; // Added for ADMIN role lookup
import com.example.PipReviewSystem.repository.EmployeeRepository;
import com.example.PipReviewSystem.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
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

    @Autowired
    private NotificationService notificationService; // NotificationService injected


    /**
     * Maps a Report entity to a ReportResponseDTO.
     * This is a helper method to convert database entities to client-friendly DTOs.
     *
     * @param r The Report entity to map.
     * @return A ReportResponseDTO object.
     */
    private ReportResponseDTO mapToDto(Report r) {

        ReportResponseDTO dto = new ReportResponseDTO();

        dto.setReportId(r.getReportId());

        dto.setReportType(r.getReportType());

        dto.setFileUrl(r.getFileUrl());

        // ✅ Add download URL (frontend will use this)

        dto.setDownloadUrl("http://localhost:8880/api/reports/" + r.getReportId() + "/download");

        dto.setGeneratedOn(r.getGeneratedOn());

        dto.setCreatedById(r.getCreatedBy().getEmployeeId());

        dto.setCreatedByName(r.getCreatedBy().getName());

        dto.setTargetEmployeeId(r.getTargetEmployee().getEmployeeId());

        dto.setTargetEmployeeName(r.getTargetEmployee().getName());

        return dto;

    }



    /**
     * Creates a new report. Only a MANAGER can create a report.
     * Sends a notification to the target employee when a new report is generated for them.
     *
     * @param dto The ReportRequestDTO containing report details.
     * @param managerEmail The email of the manager creating the report (from Principal).
     * @return The created ReportResponseDTO.
     * @throws RuntimeException if manager or target employee not found, or if creator is not a MANAGER.
     */
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

        Report savedReport = reportRepo.save(report);

        // Notification call: New report generated (ADDED)
        String notificationTitle = "New Report Generated";
        String notificationMessage = "A new report of type '" + savedReport.getReportType() + "' has been generated for you.";
        notificationService.createNotification(savedReport.getTargetEmployee(), notificationTitle, notificationMessage, "INFO");

        return mapToDto(savedReport);
    }

    /**
     * Updates an existing report.
     * Sends a notification to the target employee if their report is updated.
     *
     * @param id The ID of the report to update.
     * @param dto The ReportUpdateDTO containing updated details.
     * @return The updated ReportResponseDTO.
     * @throws RuntimeException if the report is not found.
     */
    @Override
    public ReportResponseDTO updateReport(Long id, ReportUpdateDTO dto) {
        Report report = reportRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        // Check if any fields are actually changing for notification
        boolean changesMade = false;
        if (dto.getReportType() != null && !dto.getReportType().equals(report.getReportType())) {
            report.setReportType(dto.getReportType());
            changesMade = true;
        }
        if (dto.getFileUrl() != null && !dto.getFileUrl().equals(report.getFileUrl())) {
            report.setFileUrl(dto.getFileUrl());
            changesMade = true;
        }

        Report updatedReport = reportRepo.save(report);

        // Notification call: Report updated (ADDED)
        if (changesMade) { // Only send notification if actual changes were made
            String notificationTitle = "Report Updated";
            String notificationMessage = "Your report of type '" + updatedReport.getReportType() + "' has been updated.";
            notificationService.createNotification(updatedReport.getTargetEmployee(), notificationTitle, notificationMessage, "INFO");
        }

        return mapToDto(updatedReport);
    }

    /**
     * Retrieves all reports for a specific target employee.
     *
     * @param employeeId The UUID of the target employee.
     * @return A list of ReportResponseDTOs for the target employee.
     */
    @Override
    public List<ReportResponseDTO> getReportsForEmployee(UUID employeeId) {
        return reportRepo.findByTargetEmployeeEmployeeId(employeeId)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    /**
     * Retrieves all reports created by a specific manager.
     *
     * @param managerId The UUID of the manager (creator).
     * @return A list of ReportResponseDTOs created by the manager.
     */
    @Override
    public List<ReportResponseDTO> getReportsByManager(UUID managerId) {
        return reportRepo.findByCreatedByEmployeeId(managerId)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    /**
     * Retrieves a single report by its ID.
     *
     * @param id The ID of the report.
     * @return The ReportResponseDTO.
     * @throws RuntimeException if the report is not found.
     */
    @Override
    public ReportResponseDTO getReportById(Long id) {
        return reportRepo.findById(id).map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Report not found"));
    }

    /**
     * Deletes a report by its ID.
     * Sends notifications to the target employee and all ADMINs about the deletion.
     *
     * @param id The ID of the report to delete.
     * @throws RuntimeException if the report is not found.
     */
    @Override
    public void deleteReport(Long id) {
        Report reportToDelete = reportRepo.findById(id) // Fetch entity before deleting for notification
                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + id));

        reportRepo.deleteById(id);

        // Notification to the target employee whose report was deleted (ADDED)
        String employeeNotificationTitle = "Report Deleted";
        String employeeNotificationMessage = "Your report of type '" + reportToDelete.getReportType() + "' has been deleted.";
        notificationService.createNotification(reportToDelete.getTargetEmployee(), employeeNotificationTitle, employeeNotificationMessage, "ALERT");

        // Notification to all ADMINs about the deletion (ADDED)
        List<Employee> admins = employeeRepo.findByRole(com.example.PipReviewSystem.enums.Role.ADMIN); // Use fully qualified name or import
        for (Employee admin : admins) {
            String adminNotificationTitle = "Report Deleted (Admin)";
            String adminNotificationMessage = "Report of type '" + reportToDelete.getReportType() + "' for " + reportToDelete.getTargetEmployee().getName() + " has been deleted.";
            notificationService.createNotification(admin, adminNotificationTitle, adminNotificationMessage, "ALERT");
        }
    }

    @Override
    public List<ReportResponseDTO> getAllReports() {
        return reportRepo.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }


    @Override
    public Path getReportFilePath(Long id) {
        Report report = reportRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + id));

        if (report.getFileUrl() == null || report.getFileUrl().isEmpty()) {
            throw new RuntimeException("No file attached to this report");
        }




        // Remove leading "/uploads/" if present
        String fileName = report.getFileUrl().replace("/uploads/", "");
        return Paths.get("uploads").resolve(fileName);
    }
}








//package com.example.PipReviewSystem.service;
//
//import com.example.PipReviewSystem.dto.ReportRequestDTO;
//import com.example.PipReviewSystem.dto.ReportResponseDTO;
//import com.example.PipReviewSystem.dto.ReportUpdateDTO;
//import com.example.PipReviewSystem.entity.Employee;
//import com.example.PipReviewSystem.entity.Report;
//import com.example.PipReviewSystem.repository.EmployeeRepository;
//import com.example.PipReviewSystem.repository.ReportRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@Service
//public class ReportServiceImpl implements ReportService {
//
//    @Autowired
//    private ReportRepository reportRepo;
//
//    @Autowired
//    private EmployeeRepository employeeRepo;
//
//    /**
//     * Convert Entity -> DTO
//     */
//    private ReportResponseDTO mapToDto(Report r) {
//        ReportResponseDTO dto = new ReportResponseDTO();
//        dto.setReportId(r.getReportId());
//        dto.setReportType(r.getReportType());
//        dto.setFileUrl(r.getFileUrl());
//        dto.setGeneratedOn(r.getGeneratedOn());
//
//        if (r.getCreatedBy() != null) {
//            dto.setCreatedById(r.getCreatedBy().getEmployeeId());
//            dto.setCreatedByName(r.getCreatedBy().getName());
//        }
//
//        if (r.getTargetEmployee() != null) {
//            dto.setTargetEmployeeId(r.getTargetEmployee().getEmployeeId());
//            dto.setTargetEmployeeName(r.getTargetEmployee().getName());
//        }
//
//        return dto;
//    }
//
//    /**
//     * Create a new report (only by MANAGER)
//     */
//    @Override
//    public ReportResponseDTO createReport(ReportRequestDTO dto, String managerEmail) {
//        Employee creator = employeeRepo.findByEmail(managerEmail)
//                .orElseThrow(() -> new RuntimeException("Manager not found with email: " + managerEmail));
//
//        if (!"MANAGER".equalsIgnoreCase(creator.getRole().name())) {
//            throw new RuntimeException("Only MANAGER can create reports");
//        }
//
//        Employee target = employeeRepo.findByEmployeeId(dto.getTargetEmployeeId())
//                .orElseThrow(() -> new RuntimeException("Target employee not found with ID: " + dto.getTargetEmployeeId()));
//
//        Report report = new Report();
//        report.setCreatedBy(creator);
//        report.setTargetEmployee(target);
//        report.setReportType(dto.getReportType());
//        report.setFileUrl(dto.getFileUrl());
//        report.setGeneratedOn(LocalDateTime.now());
//
//        return mapToDto(reportRepo.save(report));
//    }
//
//    /**
//     * Update existing report
//     */
//    @Override
//    public ReportResponseDTO updateReport(Long id, ReportUpdateDTO dto) {
//        Report report = reportRepo.findById(id)
//                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + id));
//
//        if (dto.getReportType() != null) {
//            report.setReportType(dto.getReportType());
//        }
//        if (dto.getFileUrl() != null) {
//            report.setFileUrl(dto.getFileUrl());
//        }
//
//        return mapToDto(reportRepo.save(report));
//    }
//
//    /**
//     * Get reports for a specific employee
//     */
//    @Override
//    public List<ReportResponseDTO> getReportsForEmployee(UUID employeeId) {
//        return reportRepo.findByTargetEmployeeEmployeeId(employeeId)
//                .stream().map(this::mapToDto).collect(Collectors.toList());
//    }
//
//    /**
//     * Get reports created by a specific manager
//     */
//    @Override
//    public List<ReportResponseDTO> getReportsByManager(UUID managerId) {
//        return reportRepo.findByCreatedByEmployeeId(managerId)
//                .stream().map(this::mapToDto).collect(Collectors.toList());
//    }
//
//    /**
//     * Get report by ID
//     */
//    @Override
//    public ReportResponseDTO getReportById(Long id) {
//        return reportRepo.findById(id)
//                .map(this::mapToDto)
//                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + id));
//    }
//
//    /**
//     * Delete report
//     */
//    @Override
//    public void deleteReport(Long id) {
//        if (!reportRepo.existsById(id)) {
//            throw new RuntimeException("Report not found with ID: " + id);
//        }
//        reportRepo.deleteById(id);
//    }
//
//    @Override
//    public List<ReportResponseDTO> getAllReports() {
//        return reportRepo.findAll()
//                .stream()
//                .map(this::mapToDto)
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * ✅ New method: return file path for download
//     */
//    @Override
//    public Path getReportFilePath(Long id) {
//        Report report = reportRepo.findById(id)
//                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + id));
//
//        if (report.getFileUrl() == null || report.getFileUrl().isEmpty()) {
//            throw new RuntimeException("No file attached to this report");
//        }
//
//        // Remove leading "/uploads/" if present
//        String fileName = report.getFileUrl().replace("/uploads/", "");
//        return Paths.get("uploads").resolve(fileName);
//    }
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

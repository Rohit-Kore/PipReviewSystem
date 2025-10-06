package com.example.PipReviewSystem.controller;

import com.example.PipReviewSystem.dto.ReportRequestDTO;
import com.example.PipReviewSystem.dto.ReportResponseDTO;
import com.example.PipReviewSystem.dto.ReportUpdateDTO;
import com.example.PipReviewSystem.service.ReportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin("*")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // === File Upload ===
    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            String folder = "uploads"; // local folder
            Files.createDirectories(Paths.get(folder));

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(folder, fileName);
            Files.write(filePath, file.getBytes());

            // Return URL (you can serve /uploads/** in WebMvcConfigurer)
            Map<String, String> response = new HashMap<>();
            response.put("url", "/uploads/" + fileName);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed: " + e.getMessage());
        }
    }

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
    @PatchMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateReport(
            @PathVariable Long id,
            @RequestParam(value = "reportType", required = false) String reportType,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        try {
            ReportUpdateDTO dto = new ReportUpdateDTO();
            dto.setReportType(reportType);

            // Handle file upload if provided
            if (file != null && !file.isEmpty()) {
                String folder = "uploads";
                Files.createDirectories(Paths.get(folder));

                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(folder, fileName);
                Files.write(filePath, file.getBytes());

                dto.setFileUrl("/uploads/" + fileName); // Save file URL in DB
            }

            return ResponseEntity.ok(reportService.updateReport(id, dto));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
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

    @PreAuthorize("hasAnyAuthority('MANAGER', 'HR', 'ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<ReportResponseDTO>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    // ===================== FILE DOWNLOAD BY REPORT ID =====================
    @PreAuthorize("hasAnyAuthority('MANAGER', 'HR', 'ADMIN')")
    @GetMapping("/{id}/download")
    public ResponseEntity<?> downloadReportById(@PathVariable Long id) {
        try {
            // Fetch report from DB
            ReportResponseDTO report = reportService.getReportById(id);

            if (report.getFileUrl() == null) {
                return ResponseEntity.badRequest().body("No file attached to this report.");
            }

            // Extract file name from stored URL (/uploads/xxxx.pdf)
            String fileName = report.getFileUrl().replace("/uploads/", "");
            Path filePath = Paths.get("uploads").resolve(fileName).normalize();

            if (!Files.exists(filePath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found: " + fileName);
            }

            byte[] fileBytes = Files.readAllBytes(filePath);
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + filePath.getFileName().toString() + "\"")
                    .header("Content-Type", contentType)
                    .body(fileBytes);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not download file: " + e.getMessage());
        }
    }
}



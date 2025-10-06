package com.example.PipReviewSystem.service;

import com.example.PipReviewSystem.dto.PerformanceReviewDTO;
import com.example.PipReviewSystem.dto.PerformanceReviewResponseDTO;
import com.example.PipReviewSystem.entity.Employee;
import com.example.PipReviewSystem.entity.PerformanceReview;
import com.example.PipReviewSystem.enums.Role; // Added for ADMIN role lookup
import com.example.PipReviewSystem.repository.EmployeeRepository;
import com.example.PipReviewSystem.repository.PerformanceReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Lombok generates constructor for final fields
public class PerformanceReviewService {

    // @Autowired is redundant with @RequiredArgsConstructor, but kept if you prefer it explicitly
    @Autowired
    private final PerformanceReviewRepository reviewRepository;
    @Autowired
    private final EmployeeRepository employeeRepository;
    @Autowired // Added: NotificationService injection
    private NotificationService notificationService;

    // Changed to List<Role> for type safety and consistency with Role enum
    private final List<Role> validReviewerRoles = Arrays.asList(Role.HR, Role.MANAGER, Role.ADMIN);

    /**
     * Maps a PerformanceReview entity to a PerformanceReviewDTO.
     * This utility method helps in converting database entities to client-friendly DTOs.
     *
     * @param review The PerformanceReview entity to map.
     * @return A PerformanceReviewDTO object.
     */
    public PerformanceReviewDTO mapToDTO(PerformanceReview review) {
        PerformanceReviewDTO dto = new PerformanceReviewDTO();
        dto.setReviewId(review.getReviewId());
        dto.setEmployeeId(review.getEmployee().getEmployeeId());
        dto.setEmployeeName(review.getEmployee().getName());
        dto.setReviewerId(review.getReviewer().getEmployeeId());
        dto.setReviewerName(review.getReviewer().getName());
        dto.setReviewPeriod(review.getReviewPeriod());
        dto.setReviewDate(review.getReviewDate());
        dto.setScores(review.getScores());
        dto.setOverallRating(review.getOverallRating());
        dto.setComments(review.getComments());
        dto.setReviewType(review.getReviewType());
        dto.setPdfUrl(review.getPdfUrl());
        return dto;
    }


    /**
     * Creates a new performance review based on the provided DTO.
     * Validates reviewer role and sends a real-time notification to the employee.
     *
     * @param dto The PerformanceReviewDTO containing review details.
     * @return The created PerformanceReviewDTO with populated IDs and names.
     */
    public PerformanceReviewDTO createReview(PerformanceReviewDTO dto) {
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Employee reviewer = employeeRepository.findById(dto.getReviewerId())
                .orElseThrow(() -> new RuntimeException("Reviewer not found"));

        validateReviewerRole(reviewer);

        PerformanceReview review = new PerformanceReview();
        review.setEmployee(employee);
        review.setReviewer(reviewer);
        review.setReviewPeriod(dto.getReviewPeriod());
        review.setReviewDate(dto.getReviewDate());
        review.setScores(dto.getScores());
        review.setOverallRating(dto.getOverallRating());
        review.setComments(dto.getComments());
        review.setReviewType(dto.getReviewType());
        review.setPdfUrl(dto.getPdfUrl());

        // Save to DB
        PerformanceReview saved = reviewRepository.save(review);

        // Notification call: New performance review created (ADDED)
        String notificationTitle = "New Performance Review";
        String notificationMessage = "A new performance review for " + saved.getReviewPeriod() + " has been completed.";
        notificationService.createNotification(saved.getEmployee(), notificationTitle, notificationMessage, "INFO");

        // Map saved entity to DTO with names and IDs for response
        return mapToDTO(saved); // Reusing mapToDTO for consistent response
    }

    /**
     * Retrieves all performance reviews, mapping them to PerformanceReviewResponseDTOs.
     *
     * @return A list of PerformanceReviewResponseDTOs.
     */
    public List<PerformanceReviewResponseDTO> getAllPerformanceReviews() {
        List<PerformanceReview> reviews = reviewRepository.findAll();

        return reviews.stream().map(review -> {
            PerformanceReviewResponseDTO dto = new PerformanceReviewResponseDTO();
            dto.setReviewId(review.getReviewId());
            dto.setEmployeeId(review.getEmployee().getEmployeeId());
            dto.setEmployeeName(review.getEmployee().getName());
            dto.setReviewerId(review.getReviewer().getEmployeeId());
            dto.setReviewerName(review.getReviewer().getName());
            dto.setReviewPeriod(review.getReviewPeriod());
            dto.setReviewDate(review.getReviewDate());
            dto.setScores(review.getScores());
            dto.setOverallRating(review.getOverallRating());
            dto.setComments(review.getComments());
            dto.setReviewType(review.getReviewType());
            dto.setPdfUrl(review.getPdfUrl());
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * Retrieves a single performance review by its ID.
     *
     * @param id The ID of the review.
     * @return The PerformanceReview entity.
     * @throws RuntimeException if the review is not found.
     */
    public PerformanceReview getReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with ID: " + id));
    }

    /**
     * Updates an existing performance review with new details from a DTO.
     *
     * @param id The ID of the review to update.
     * @param dto The PerformanceReviewDTO containing updated details.
     * @return The updated PerformanceReviewDTO.
     * @throws RuntimeException if employee or reviewer not found.
     */
    public PerformanceReviewDTO updateReview(Long id, PerformanceReviewDTO dto) {
        PerformanceReview review = getReviewById(id);

        if (dto.getEmployeeId() != null) {
            Employee employee = employeeRepository.findById(dto.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            review.setEmployee(employee);
        }

        if (dto.getReviewerId() != null) {
            Employee reviewer = employeeRepository.findById(dto.getReviewerId())
                    .orElseThrow(() -> new RuntimeException("Reviewer not found"));
            validateReviewerRole(reviewer);
            review.setReviewer(reviewer);
        }

        review.setReviewPeriod(dto.getReviewPeriod());
        review.setReviewDate(dto.getReviewDate());
        review.setScores(dto.getScores());
        review.setOverallRating(dto.getOverallRating());
        review.setComments(dto.getComments());
        review.setReviewType(dto.getReviewType());
        review.setPdfUrl(dto.getPdfUrl());

        PerformanceReview updated = reviewRepository.save(review);

        // Notification call: Performance review updated (ADDED)
        String notificationTitle = "Performance Review Updated";
        String notificationMessage = "Your performance review for " + updated.getReviewPeriod() + " has been updated.";
        notificationService.createNotification(updated.getEmployee(), notificationTitle, notificationMessage, "INFO");

        // Map to DTO with names for response
        return mapToDTO(updated); // Reusing mapToDTO for consistent response
    }

    /**
     * Deletes a performance review by its ID.
     * Sends notifications to the employee whose review was deleted, and to all ADMINs.
     *
     * @param id The ID of the review to delete.
     * @throws RuntimeException if the review is not found.
     */
    public void deleteReview(Long id) { // Removed @Override annotation
        PerformanceReview reviewToDelete = reviewRepository.findById(id) // Fetch entity before deleting for notification
                .orElseThrow(() -> new RuntimeException("Review not found with ID: " + id));

        reviewRepository.deleteById(id);

        // Notification to the employee whose review was deleted (ADDED)
        String employeeNotificationTitle = "Performance Review Deleted";
        String employeeNotificationMessage = "Your performance review for " + reviewToDelete.getReviewPeriod() + " has been deleted.";
        notificationService.createNotification(reviewToDelete.getEmployee(), employeeNotificationTitle, employeeNotificationMessage, "ALERT");

        // Notification to all ADMINs about the deletion (ADDED)
        List<Employee> admins = employeeRepository.findByRole(Role.ADMIN);
        for (Employee admin : admins) {
            String adminNotificationTitle = "Performance Review Deleted (Admin)";
            String adminNotificationMessage = "Performance review for " + reviewToDelete.getEmployee().getName() + " (" + reviewToDelete.getReviewPeriod() + ") has been deleted.";
            notificationService.createNotification(admin, adminNotificationTitle, adminNotificationMessage, "ALERT");
        }
    }

    /**
     * Retrieves a list of performance reviews for a specific employee.
     *
     * @param employeeId The UUID of the employee.
     * @return A list of PerformanceReview entities.
     */
    public List<PerformanceReview> getReviewsByEmployeeId(UUID employeeId) {
        return reviewRepository.findByEmployeeEmployeeId(employeeId);
    }

    /**
     * Retrieves a list of performance reviews conducted by a specific reviewer.
     *
     * @param reviewerId The UUID of the reviewer.
     * @return A list of PerformanceReview entities.
     */
    public List<PerformanceReview> getReviewsByReviewerId(UUID reviewerId) {
        return reviewRepository.findByReviewerEmployeeId(reviewerId);
    }

    /**
     * Retrieves a list of performance reviews based on their review type.
     *
     * @param reviewType The type of review (e.g., "MID_YEAR", "ANNUAL").
     * @return A list of PerformanceReview entities.
     */
    public List<PerformanceReview> getReviewsByReviewType(String reviewType) {
        return reviewRepository.findByReviewType(reviewType);
    }

    /**
     * Retrieves a list of performance reviews based on their review period.
     *
     * @param reviewPeriod The review period (e.g., "Q1 2024", "FY 2023").
     * @return A list of PerformanceReview entities.
     */
    public List<PerformanceReview> getReviewsByReviewPeriod(String reviewPeriod) {
        return reviewRepository.findByReviewPeriod(reviewPeriod);
    }

    /**
     * Retrieves a list of performance reviews within a specified overall rating range.
     *
     * @param min The minimum overall rating.
     * @param max The maximum overall rating.
     * @return A list of PerformanceReview entities filtered by rating.
     */
    public List<PerformanceReview> getReviewsByRatingRange(double min, double max) {
        return reviewRepository.findAll().stream()
                .filter(r -> r.getOverallRating() != null && r.getOverallRating() >= min && r.getOverallRating() <= max)
                .collect(Collectors.toList());
    }

    /**
     * Validates if the provided employee has a valid role (ADMIN, HR, or MANAGER) to be a reviewer.
     *
     * @param reviewer The Employee object to validate.
     * @throws RuntimeException if the reviewer's role is invalid.
     */
    private void validateReviewerRole(Employee reviewer) {
        // Ensure reviewer.getRole() returns a Role enum. If it returns String, you might need Role.valueOf(reviewer.getRole().toUpperCase()).
        if (!validReviewerRoles.contains(reviewer.getRole())) {
            throw new RuntimeException("Reviewer must be ADMIN, HR, or MANAGER");
        }
    }
}




//package com.example.PipReviewSystem.service;
//
//import com.example.PipReviewSystem.dto.PerformanceReviewDTO;
//import com.example.PipReviewSystem.dto.PerformanceReviewResponseDTO;
//import com.example.PipReviewSystem.entity.Employee;
//import com.example.PipReviewSystem.entity.PerformanceReview;
//import com.example.PipReviewSystem.enums.Role;
//import com.example.PipReviewSystem.repository.EmployeeRepository;
//import com.example.PipReviewSystem.repository.PerformanceReviewRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class PerformanceReviewService {
//
//    private final PerformanceReviewRepository reviewRepository;
//    private final EmployeeRepository employeeRepository;
//
//    private final List<Role> validReviewerRoles = Arrays.asList(Role.HR, Role.MANAGER, Role.ADMIN);
//
// /*   public PerformanceReview createReview(PerformanceReviewDTO dto) {
//        Employee employee = employeeRepository.findById(dto.getEmployeeId())
//                .orElseThrow(() -> new RuntimeException("Employee not found"));
//
//        Employee reviewer = employeeRepository.findById(dto.getReviewerId())
//                .orElseThrow(() -> new RuntimeException("Reviewer not found"));
//
//        validateReviewerRole(reviewer);
//
//        PerformanceReview review = new PerformanceReview();
//        review.setEmployee(employee);
//        review.setReviewer(reviewer);
//        review.setReviewPeriod(dto.getReviewPeriod());
//        review.setReviewDate(dto.getReviewDate());
//        review.setScores(dto.getScores());
//        review.setOverallRating(dto.getOverallRating());
//        review.setComments(dto.getComments());
//        review.setReviewType(dto.getReviewType());
//        review.setPdfUrl(dto.getPdfUrl());
//
//
//
//        return reviewRepository.save(review);
//    }
//*/
// public PerformanceReviewDTO mapToDTO(PerformanceReview review) {
//     PerformanceReviewDTO dto = new PerformanceReviewDTO();
//     dto.setReviewId(review.getReviewId());
//     dto.setEmployeeId(review.getEmployee().getEmployeeId());
//     dto.setEmployeeName(review.getEmployee().getName());
//     dto.setReviewerId(review.getReviewer().getEmployeeId());
//     dto.setReviewerName(review.getReviewer().getName());
//     dto.setReviewPeriod(review.getReviewPeriod());
//     dto.setReviewDate(review.getReviewDate());
//     dto.setScores(review.getScores());
//     dto.setOverallRating(review.getOverallRating());
//     dto.setComments(review.getComments());
//     dto.setReviewType(review.getReviewType());
//     dto.setPdfUrl(review.getPdfUrl());
//     return dto;
// }
//
//
//    public PerformanceReviewDTO createReview(PerformanceReviewDTO dto) {
//     Employee employee = employeeRepository.findById(dto.getEmployeeId())
//             .orElseThrow(() -> new RuntimeException("Employee not found"));
//
//     Employee reviewer = employeeRepository.findById(dto.getReviewerId())
//             .orElseThrow(() -> new RuntimeException("Reviewer not found"));
//
//     validateReviewerRole(reviewer);
//
//     PerformanceReview review = new PerformanceReview();
//     review.setEmployee(employee);
//     review.setReviewer(reviewer);
//     review.setReviewPeriod(dto.getReviewPeriod());
//     review.setReviewDate(dto.getReviewDate());
//     review.setScores(dto.getScores());
//     review.setOverallRating(dto.getOverallRating());
//     review.setComments(dto.getComments());
//     review.setReviewType(dto.getReviewType());
//     review.setPdfUrl(dto.getPdfUrl());
//
//     // Save to DB
//     PerformanceReview saved = reviewRepository.save(review);
//
//     // ✅ Map saved entity to DTO with names and IDs
//     PerformanceReviewDTO response = new PerformanceReviewDTO();
//     response.setReviewId(saved.getReviewId());
//     response.setEmployeeId(employee.getEmployeeId());
//     response.setEmployeeName(employee.getName());
//     response.setReviewerId(reviewer.getEmployeeId());
//     response.setReviewerName(reviewer.getName());
//     response.setReviewPeriod(saved.getReviewPeriod());
//     response.setReviewDate(saved.getReviewDate());
//     response.setScores(saved.getScores());
//     response.setOverallRating(saved.getOverallRating());
//     response.setComments(saved.getComments());
//     response.setReviewType(saved.getReviewType());
//     response.setPdfUrl(saved.getPdfUrl());
//
//     return response;
// }
//
///*
//
//    public List<PerformanceReview> getAllReviews() {
//        return reviewRepository.findAll();
//    }
//*/
//
//    public List<PerformanceReviewResponseDTO> getAllPerformanceReviews() {
//        List<PerformanceReview> reviews = reviewRepository.findAll();
//
//        return reviews.stream().map(review -> {
//            PerformanceReviewResponseDTO dto = new PerformanceReviewResponseDTO();
//            dto.setReviewId(review.getReviewId());
//            dto.setEmployeeId(review.getEmployee().getEmployeeId());
//            dto.setEmployeeName(review.getEmployee().getName()); // assuming getFullName() returns name
//            dto.setReviewerId(review.getReviewer().getEmployeeId());
//            dto.setReviewerName(review.getReviewer().getName()); // same here
//            dto.setReviewPeriod(review.getReviewPeriod());
//            dto.setReviewDate(review.getReviewDate());
//            dto.setScores(review.getScores());
//            dto.setOverallRating(review.getOverallRating());
//            dto.setComments(review.getComments());
//            dto.setReviewType(review.getReviewType());
//            dto.setPdfUrl(review.getPdfUrl());
//            return dto;
//        }).collect(Collectors.toList());
//    }
//
//
//
//
//
//
//    public PerformanceReview getReviewById(Long id) {
//        return reviewRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Review not found with ID: " + id));
//    }
///*
//
//    public PerformanceReview updateReview(Long id, PerformanceReviewDTO dto) {
//        PerformanceReview review = getReviewById(id);
//
//        if (dto.getEmployeeId() != null) {
//            Employee employee = employeeRepository.findById(dto.getEmployeeId())
//                    .orElseThrow(() -> new RuntimeException("Employee not found"));
//            review.setEmployee(employee);
//        }
//
//        if (dto.getReviewerId() != null) {
//            Employee reviewer = employeeRepository.findById(dto.getReviewerId())
//                    .orElseThrow(() -> new RuntimeException("Reviewer not found"));
//            validateReviewerRole(reviewer);
//            review.setReviewer(reviewer);
//        }
//
//        review.setReviewPeriod(dto.getReviewPeriod());
//        review.setReviewDate(dto.getReviewDate());
//        review.setScores(dto.getScores());
//        review.setOverallRating(dto.getOverallRating());
//        review.setComments(dto.getComments());
//        review.setReviewType(dto.getReviewType());
//        review.setPdfUrl(dto.getPdfUrl());
//
//        return reviewRepository.save(review);
//    }
//*/
//
//    public PerformanceReviewDTO updateReview(Long id, PerformanceReviewDTO dto) {
//        PerformanceReview review = getReviewById(id);
//
//        if (dto.getEmployeeId() != null) {
//            Employee employee = employeeRepository.findById(dto.getEmployeeId())
//                    .orElseThrow(() -> new RuntimeException("Employee not found"));
//            review.setEmployee(employee);
//        }
//
//        if (dto.getReviewerId() != null) {
//            Employee reviewer = employeeRepository.findById(dto.getReviewerId())
//                    .orElseThrow(() -> new RuntimeException("Reviewer not found"));
//            validateReviewerRole(reviewer);
//            review.setReviewer(reviewer);
//        }
//
//        review.setReviewPeriod(dto.getReviewPeriod());
//        review.setReviewDate(dto.getReviewDate());
//        review.setScores(dto.getScores());
//        review.setOverallRating(dto.getOverallRating());
//        review.setComments(dto.getComments());
//        review.setReviewType(dto.getReviewType());
//        review.setPdfUrl(dto.getPdfUrl());
//
//        PerformanceReview updated = reviewRepository.save(review);
//
//        // ✅ Map to DTO with names
//        PerformanceReviewDTO response = new PerformanceReviewDTO();
//        response.setReviewId(updated.getReviewId());
//        response.setEmployeeId(updated.getEmployee().getEmployeeId());
//        response.setEmployeeName(updated.getEmployee().getName());
//        response.setReviewerId(updated.getReviewer().getEmployeeId());
//        response.setReviewerName(updated.getReviewer().getName());
//        response.setReviewPeriod(updated.getReviewPeriod());
//        response.setReviewDate(updated.getReviewDate());
//        response.setScores(updated.getScores());
//        response.setOverallRating(updated.getOverallRating());
//        response.setComments(updated.getComments());
//        response.setReviewType(updated.getReviewType());
//        response.setPdfUrl(updated.getPdfUrl());
//
//        return response;
//    }
//
//    public void deleteReview(Long id) {
//        if (!reviewRepository.existsById(id)) {
//            throw new RuntimeException("Review not found with ID: " + id);
//        }
//        reviewRepository.deleteById(id);
//    }
//
//    public List<PerformanceReview> getReviewsByEmployeeId(UUID employeeId) {
//        return reviewRepository.findByEmployeeEmployeeId(employeeId);
//    }
//
//    public List<PerformanceReview> getReviewsByReviewerId(UUID reviewerId) {
//        return reviewRepository.findByReviewerEmployeeId(reviewerId);
//    }
//
//    public List<PerformanceReview> getReviewsByReviewType(String reviewType) {
//        return reviewRepository.findByReviewType(reviewType);
//    }
//
//    public List<PerformanceReview> getReviewsByReviewPeriod(String reviewPeriod) {
//        return reviewRepository.findByReviewPeriod(reviewPeriod);
//    }
//
//    public List<PerformanceReview> getReviewsByRatingRange(double min, double max) {
//        return reviewRepository.findAll().stream()
//                .filter(r -> r.getOverallRating() != null && r.getOverallRating() >= min && r.getOverallRating() <= max)
//                .collect(Collectors.toList());
//    }
//
//    private void validateReviewerRole(Employee reviewer) {
//        if (!validReviewerRoles.contains(reviewer.getRole())) {
//            throw new RuntimeException("Reviewer must be ADMIN, HR, or MANAGER");
//        }
//    }
//}
//
//
//
//
//
//
//

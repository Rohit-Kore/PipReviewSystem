package com.example.PipReviewSystem.service;

import com.example.PipReviewSystem.dto.PerformanceReviewDTO;
import com.example.PipReviewSystem.entity.Employee;
import com.example.PipReviewSystem.entity.PerformanceReview;
import com.example.PipReviewSystem.enums.Role;
import com.example.PipReviewSystem.repository.EmployeeRepository;
import com.example.PipReviewSystem.repository.PerformanceReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerformanceReviewService {

    private final PerformanceReviewRepository reviewRepository;
    private final EmployeeRepository employeeRepository;

    private final List<Role> validReviewerRoles = Arrays.asList(Role.HR, Role.MANAGER, Role.ADMIN);

    public PerformanceReview createReview(PerformanceReviewDTO dto) {
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

        return reviewRepository.save(review);
    }

    public List<PerformanceReview> getAllReviews() {
        return reviewRepository.findAll();
    }

    public PerformanceReview getReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with ID: " + id));
    }

    public PerformanceReview updateReview(Long id, PerformanceReviewDTO dto) {
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

        return reviewRepository.save(review);
    }

    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new RuntimeException("Review not found with ID: " + id);
        }
        reviewRepository.deleteById(id);
    }

    public List<PerformanceReview> getReviewsByEmployeeId(UUID employeeId) {
        return reviewRepository.findByEmployeeEmployeeId(employeeId);
    }

    public List<PerformanceReview> getReviewsByReviewerId(UUID reviewerId) {
        return reviewRepository.findByReviewerEmployeeId(reviewerId);
    }

    public List<PerformanceReview> getReviewsByReviewType(String reviewType) {
        return reviewRepository.findByReviewType(reviewType);
    }

    public List<PerformanceReview> getReviewsByReviewPeriod(String reviewPeriod) {
        return reviewRepository.findByReviewPeriod(reviewPeriod);
    }

    public List<PerformanceReview> getReviewsByRatingRange(double min, double max) {
        return reviewRepository.findAll().stream()
                .filter(r -> r.getOverallRating() != null && r.getOverallRating() >= min && r.getOverallRating() <= max)
                .collect(Collectors.toList());
    }

    private void validateReviewerRole(Employee reviewer) {
        if (!validReviewerRoles.contains(reviewer.getRole())) {
            throw new RuntimeException("Reviewer must be ADMIN, HR, or MANAGER");
        }
    }
}
































//package com.example.PipReviewSystem.service;
//
//import com.example.PipReviewSystem.dto.PerformanceReviewDTO;
//import com.example.PipReviewSystem.entity.Employee;
//import com.example.PipReviewSystem.entity.PerformanceReview;
//import com.example.PipReviewSystem.repository.EmployeeRepository;
//import com.example.PipReviewSystem.repository.PerformanceReviewRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//public class PerformanceReviewService {
//
//    private final PerformanceReviewRepository reviewRepository;
//    private final EmployeeRepository employeeRepository;
//
//    private final List<String> validReviewerRoles = Arrays.asList("ADMIN", "HR", "MANAGER");
//
//    public PerformanceReview createReview(PerformanceReviewDTO dto) {
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
//        return reviewRepository.save(review);
//    }
//
//    public List<PerformanceReview> getAllReviews() {
//        return reviewRepository.findAll();
//    }
//
//    public PerformanceReview getReviewById(Long id) {
//        return reviewRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Review not found with ID: " + id));
//    }
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
//    // 🔒 Validation: only ADMIN, HR, or MANAGER can be reviewers
//    private void validateReviewerRole(Employee reviewer) {
//        String role = reviewer.getRole(); // assuming getRole() returns a string like "HR"
//        if (role == null || !validReviewerRoles.contains(role.toUpperCase())) {
//            throw new RuntimeException("Reviewer must have one of the roles: ADMIN, HR, or MANAGER");
//        }
//    }
//}

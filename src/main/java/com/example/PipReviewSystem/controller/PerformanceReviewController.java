package com.example.PipReviewSystem.controller;

import com.example.PipReviewSystem.dto.PerformanceReviewDTO;
import com.example.PipReviewSystem.entity.PerformanceReview;
import com.example.PipReviewSystem.service.PerformanceReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performance-reviews")
@RequiredArgsConstructor
public class PerformanceReviewController {

    private final PerformanceReviewService reviewService;

    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody PerformanceReviewDTO dto) {
        try {
            PerformanceReview created = reviewService.createReview(dto);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error creating review: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllReviews() {
        try {
            List<PerformanceReview> reviews = reviewService.getAllReviews();
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to fetch reviews");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReviewById(@PathVariable Long id) {
        try {
            PerformanceReview review = reviewService.getReviewById(id);
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Review not found: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Long id, @RequestBody PerformanceReviewDTO dto) {
        try {
            PerformanceReview updated = reviewService.updateReview(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Update failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        try {
            reviewService.deleteReview(id);
            return ResponseEntity.ok("Review deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Delete failed: " + e.getMessage());
        }
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getReviewsByEmployee(@PathVariable Long employeeId) {
        try {
            List<PerformanceReview> list = reviewService.getReviewsByEmployeeId(employeeId);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching employee reviews: " + e.getMessage());
        }
    }

    @GetMapping("/reviewer/{reviewerId}")
    public ResponseEntity<?> getReviewsByReviewer(@PathVariable Long reviewerId) {
        try {
            List<PerformanceReview> list = reviewService.getReviewsByReviewerId(reviewerId);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching reviewer reviews: " + e.getMessage());
        }
    }

    @GetMapping("/type/{reviewType}")
    public ResponseEntity<?> getReviewsByType(@PathVariable String reviewType) {
        try {
            List<PerformanceReview> list = reviewService.getReviewsByReviewType(reviewType);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching reviews by type: " + e.getMessage());
        }
    }

    @GetMapping("/period/{reviewPeriod}")
    public ResponseEntity<?> getReviewsByPeriod(@PathVariable String reviewPeriod) {
        try {
            List<PerformanceReview> list = reviewService.getReviewsByReviewPeriod(reviewPeriod);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching reviews by period: " + e.getMessage());
        }
    }
}

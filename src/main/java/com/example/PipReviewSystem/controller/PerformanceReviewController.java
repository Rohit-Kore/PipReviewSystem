// PerformanceReviewController.java
package com.example.PipReviewSystem.controller;

import com.example.PipReviewSystem.dto.PerformanceReviewDTO;
import com.example.PipReviewSystem.dto.PerformanceReviewResponseDTO;
import com.example.PipReviewSystem.entity.PerformanceReview;
import com.example.PipReviewSystem.service.PerformanceReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/performance-reviews")
@RequiredArgsConstructor
public class PerformanceReviewController {

    private final PerformanceReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('HR', 'MANAGER', 'ADMIN')")
    public ResponseEntity<?> createReview(@RequestBody PerformanceReviewDTO dto) {
        try {
            PerformanceReviewDTO created = reviewService.createReview(dto);
            return ResponseEntity.ok(List.of(created)); // Wrap in list if needed
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error creating review: " + e.getMessage());
        }
    }



    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PerformanceReviewResponseDTO>> getAllReviews() {
        List<PerformanceReviewResponseDTO> reviews = reviewService.getAllPerformanceReviews();
        return ResponseEntity.ok(reviews);
    }


/*   @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllReviews() {
        try {
            return ResponseEntity.ok(reviewService.getAllReviews());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to fetch reviews");
        }
    }*/

   /* @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR', 'ADMIN')")
    public ResponseEntity<?> getReviewById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(reviewService.getReviewById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Review not found: " + e.getMessage());
        }
    }*/
   @GetMapping("/{id}")
   @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR', 'ADMIN')")
   public ResponseEntity<PerformanceReviewDTO> getReviewById(@PathVariable Long id) {
       PerformanceReview review = reviewService.getReviewById(id);
       PerformanceReviewDTO dto = reviewService.mapToDTO(review);
       return ResponseEntity.ok(dto);
   }




    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER', 'ADMIN')")
    public ResponseEntity<?> updateReview(@PathVariable Long id, @RequestBody PerformanceReviewDTO dto) {
        try {
            return ResponseEntity.ok(reviewService.updateReview(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Update failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        try {
            reviewService.deleteReview(id);
            return ResponseEntity.ok("Review deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Delete failed: " + e.getMessage());
        }
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR', 'ADMIN')")
    public ResponseEntity<?> getReviewsByEmployee(@PathVariable UUID employeeId) {
        try {
            return ResponseEntity.ok(reviewService.getReviewsByEmployeeId(employeeId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching employee reviews: " + e.getMessage());
        }
    }

    @GetMapping("/reviewer/{reviewerId}")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER', 'ADMIN')")
    public ResponseEntity<?> getReviewsByReviewer(@PathVariable UUID reviewerId) {
        try {
            return ResponseEntity.ok(reviewService.getReviewsByReviewerId(reviewerId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching reviewer reviews: " + e.getMessage());
        }
    }

    @GetMapping("/type/{reviewType}")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER', 'ADMIN')")
    public ResponseEntity<?> getReviewsByType(@PathVariable String reviewType) {
        try {
            return ResponseEntity.ok(reviewService.getReviewsByReviewType(reviewType));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching reviews by type: " + e.getMessage());
        }
    }

    @GetMapping("/period/{reviewPeriod}")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER', 'ADMIN')")
    public ResponseEntity<?> getReviewsByPeriod(@PathVariable String reviewPeriod) {
        try {
            return ResponseEntity.ok(reviewService.getReviewsByReviewPeriod(reviewPeriod));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching reviews by period: " + e.getMessage());
        }
    }

    @GetMapping("/rating-range")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER', 'ADMIN')")
    public ResponseEntity<?> getReviewsByRatingRange(@RequestParam double min, @RequestParam double max) {
        try {
            return ResponseEntity.ok(reviewService.getReviewsByRatingRange(min, max));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching reviews by rating range: " + e.getMessage());
        }
    }
}
































//package com.example.PipReviewSystem.controller;
//
//import com.example.PipReviewSystem.dto.PerformanceReviewDTO;
//import com.example.PipReviewSystem.entity.PerformanceReview;
//import com.example.PipReviewSystem.service.PerformanceReviewService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api/performance-reviews")
//@RequiredArgsConstructor
//public class PerformanceReviewController {
//
//    private final PerformanceReviewService reviewService;
//
//    // ✅ Only REVIEWER or ADMIN can create a review
//    @PostMapping
//    @PreAuthorize("hasAnyRole('REVIEWER', 'ADMIN')")
//    public ResponseEntity<?> createReview(@RequestBody PerformanceReviewDTO dto) {
//        try {
//            PerformanceReview created = reviewService.createReview(dto);
//            return ResponseEntity.ok(created);
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body("Error creating review: " + e.getMessage());
//        }
//    }
//
//    // ✅ All authenticated users can view all reviews (optional: restrict to ADMIN only)
//    @GetMapping
//    @PreAuthorize("hasAnyRole('ADMIN')")
//    public ResponseEntity<?> getAllReviews() {
//        try {
//            List<PerformanceReview> reviews = reviewService.getAllReviews();
//            return ResponseEntity.ok(reviews);
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body("Failed to fetch reviews");
//        }
//    }
//
//    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyRole('REVIEWER', 'EMPLOYEE', 'ADMIN')")
//    public ResponseEntity<?> getReviewById(@PathVariable Long id) {
//        try {
//            PerformanceReview review = reviewService.getReviewById(id);
//            return ResponseEntity.ok(review);
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(404).body("Review not found: " + e.getMessage());
//        }
//    }
//
//    // ✅ Only REVIEWER or ADMIN can update a review
//    @PutMapping("/{id}")
//    @PreAuthorize("hasAnyRole('REVIEWER', 'ADMIN')")
//    public ResponseEntity<?> updateReview(@PathVariable Long id, @RequestBody PerformanceReviewDTO dto) {
//        try {
//            PerformanceReview updated = reviewService.updateReview(id, dto);
//            return ResponseEntity.ok(updated);
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body("Update failed: " + e.getMessage());
//        }
//    }
//
//    // ✅ Only ADMIN can delete a review
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
//        try {
//            reviewService.deleteReview(id);
//            return ResponseEntity.ok("Review deleted successfully.");
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(404).body("Delete failed: " + e.getMessage());
//        }
//    }
//
//    // ✅ EMPLOYEE can see their reviews, REVIEWER and ADMIN can see anyone's
//    @GetMapping("/employee/{employeeId}")
//    @PreAuthorize("hasAnyRole('EMPLOYEE', 'REVIEWER', 'ADMIN')")
//    public ResponseEntity<?> getReviewsByEmployee(@PathVariable UUID employeeId) {
//        try {
//            List<PerformanceReview> list = reviewService.getReviewsByEmployeeId(employeeId);
//            return ResponseEntity.ok(list);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Error fetching employee reviews: " + e.getMessage());
//        }
//    }
//
//    @GetMapping("/reviewer/{reviewerId}")
//    @PreAuthorize("hasAnyRole('REVIEWER', 'ADMIN')")
//    public ResponseEntity<?> getReviewsByReviewer(@PathVariable UUID reviewerId) {
//        try {
//            List<PerformanceReview> list = reviewService.getReviewsByReviewerId(reviewerId);
//            return ResponseEntity.ok(list);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Error fetching reviewer reviews: " + e.getMessage());
//        }
//    }
//
//    @GetMapping("/type/{reviewType}")
//    @PreAuthorize("hasAnyRole('REVIEWER', 'ADMIN')")
//    public ResponseEntity<?> getReviewsByType(@PathVariable String reviewType) {
//        try {
//            List<PerformanceReview> list = reviewService.getReviewsByReviewType(reviewType);
//            return ResponseEntity.ok(list);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Error fetching reviews by type: " + e.getMessage());
//        }
//    }
//
//    @GetMapping("/period/{reviewPeriod}")
//    @PreAuthorize("hasAnyRole('REVIEWER', 'ADMIN')")
//    public ResponseEntity<?> getReviewsByPeriod(@PathVariable String reviewPeriod) {
//        try {
//            List<PerformanceReview> list = reviewService.getReviewsByReviewPeriod(reviewPeriod);
//            return ResponseEntity.ok(list);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Error fetching reviews by period: " + e.getMessage());
//        }
//    }
//}

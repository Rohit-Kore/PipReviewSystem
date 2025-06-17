package com.example.PipReviewSystem.repository;

import com.example.PipReviewSystem.entity.PerformanceReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Long> {
    List<PerformanceReview> findByEmployeeEmployeeId(Long employeeId);
    List<PerformanceReview> findByReviewerEmployeeId(Long reviewerId);
    List<PerformanceReview> findByReviewType(String reviewType);
    List<PerformanceReview> findByReviewPeriod(String reviewPeriod);
}

package com.example.PipReviewSystem.repository;

import com.example.PipReviewSystem.entity.PerformanceReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Long> {
    List<PerformanceReview> findByEmployeeEmployeeId(UUID employeeId);
    List<PerformanceReview> findByReviewerEmployeeId(UUID reviewerId);
    List<PerformanceReview> findByReviewType(String reviewType);
    List<PerformanceReview> findByReviewPeriod(String reviewPeriod);
}



























//package com.example.PipReviewSystem.repository;
//
//import com.example.PipReviewSystem.entity.PerformanceReview;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.UUID;
//
//@Repository
//public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Long> {
//    List<PerformanceReview> findByEmployeeEmployeeId(UUID employeeId);
//    //List<PerformanceReview> findByReviewerEmployeeId(Long reviewerId);
//    List<PerformanceReview> findByReviewType(String reviewType);
//    List<PerformanceReview> findByReviewPeriod(String reviewPeriod);
//
//
//    List<PerformanceReview> findByReviewerEmployeeId(UUID employeeId);
//
//}

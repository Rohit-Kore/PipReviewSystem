package com.example.PipReviewSystem.repository;

import com.example.PipReviewSystem.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByCreatedByEmployeeId(Long userId);
    List<Report> findByReportType(String reportType);
}

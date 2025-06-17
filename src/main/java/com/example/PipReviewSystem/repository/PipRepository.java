package com.example.PipReviewSystem.repository;

import com.example.PipReviewSystem.entity.PIP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PipRepository extends JpaRepository<PIP, Long> {
    List<PIP> findByEmployeeEmployeeId(Long employeeId);
    List<PIP> findByReviewerEmployeeId(Long reviewerId);
    List<PIP> findByStatus(String status);
}

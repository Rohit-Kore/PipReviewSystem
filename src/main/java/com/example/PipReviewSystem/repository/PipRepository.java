package com.example.PipReviewSystem.repository;

import com.example.PipReviewSystem.entity.PIP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PipRepository extends JpaRepository<PIP, Long> {
//    List<PIP> findByEmployeeEmployeeId(UUID employeeId);
//    List<PIP> findByReviewerEmployeeId(UUID reviewerId);
//    List<PIP> findByStatus(String status);

        List<PIP> findByEmployee_EmployeeId(UUID employeeId);
}

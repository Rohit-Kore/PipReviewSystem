package com.example.PipReviewSystem.repository;

import com.example.PipReviewSystem.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.example.PipReviewSystem.enums.Role;

import java.util.UUID;
@Repository
    public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
        Optional<Employee> findByEmail(String email);
        Optional<Employee> findByEmployeeId(UUID id);
        List<Employee> findByRole(Role role);

        //extra

       List<Employee> findByStatus(String status);
       List<Employee> findByManagerId(UUID managerId);
       List<Employee> findByJoiningDateBetween(LocalDateTime start, LocalDateTime end);



}





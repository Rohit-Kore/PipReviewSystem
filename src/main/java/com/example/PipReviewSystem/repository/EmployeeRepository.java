package com.example.PipReviewSystem.repository;

import com.example.PipReviewSystem.entity.Employee;
import com.example.PipReviewSystem.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByEmployeeId(UUID id);

    List<Employee> findByRole(Role role);

    List<Employee> findByStatus(String status);

    // Now using the proper JPA relationship
    List<Employee> findByManager_EmployeeId(UUID managerId);

    // This new method allows us to directly find team members by the manager's email
    List<Employee> findByManager_Email(String managerEmail);

    List<Employee> findByJoiningDateBetween(LocalDateTime start, LocalDateTime end);

    Optional<Employee> findByPasswordResetToken(String token);

    List<Employee> findByManagerId(UUID managerId);
}

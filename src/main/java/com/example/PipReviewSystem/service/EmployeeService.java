package com.example.PipReviewSystem.service;

import com.example.PipReviewSystem.entity.Employee;
import com.example.PipReviewSystem.enums.Role;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {
        ResponseEntity<?> registerEmployee(Employee employee,String creatorRole);

        ResponseEntity<?> login(String email, String password);

        ResponseEntity<?> getAllEmployees();

        ResponseEntity<?> getEmployeeById(UUID id);

        ResponseEntity<?> updateEmployee(UUID id, Employee employee);

        ResponseEntity<?> deleteEmployee(UUID id);
        ResponseEntity<?> resetPassword(UUID employeeId, String oldPassword, String newPassword);


        //extra
        ResponseEntity<?> getEmployeesByStatus(String status);

        ResponseEntity<?> getEmployeesByRole(String role);

        ResponseEntity<?> assignManager(UUID employeeId, UUID managerId);

        ResponseEntity<?> getTeamMembers(UUID managerId);

        ResponseEntity<?> updateStatus(UUID id, String status);

        ResponseEntity<?> logout(String email);

        ResponseEntity<?> forgotPassword(String email, String newPassword);

        ResponseEntity<?> addEmployeeToPip(UUID employeeId);

        ResponseEntity<?> getPipStatus(UUID employeeId);

        ResponseEntity<?> requestPasswordResetOtp(String email);
        ResponseEntity<?> verifyOtpAndResetPassword(String email, String otp, String newPassword);


        ResponseEntity<?> requestPasswordResetLink(String email, String baseUrl); // Added this
        ResponseEntity<?> resetPasswordWithToken(String token, String newPassword); // Added this

}

//    Employee createEmployee(Employee employee);
//
//    Employee getEmployeeById(UUID id);
//
//    List<Employee> getAllEmployees();
//
//    List<Employee> getEmployeesByRole(Role role);
//
//    Employee updateEmployee(UUID id, Employee employee);
//
//    void deleteEmployee(UUID id);


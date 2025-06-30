package com.example.PipReviewSystem.service;

import com.example.PipReviewSystem.entity.Employee;
import com.example.PipReviewSystem.enums.Role;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {
        ResponseEntity<?> registerEmployee(Employee employee);
        ResponseEntity<?> login(String email, String password);
        ResponseEntity<?> getAllEmployees();
        ResponseEntity<?> getEmployeeById(UUID id);
        ResponseEntity<?> updateEmployee(UUID id, Employee employee);
        ResponseEntity<?> deleteEmployee(UUID id);
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


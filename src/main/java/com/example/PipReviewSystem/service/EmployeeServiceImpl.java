package com.example.PipReviewSystem.service;

import com.example.PipReviewSystem.config.JwtUtil;
import com.example.PipReviewSystem.entity.Employee;
import com.example.PipReviewSystem.enums.Role;
import com.example.PipReviewSystem.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;




    @Override
    public ResponseEntity<?> registerEmployee(Employee employee) {
        Optional<Employee> existing = employeeRepository.findByEmail(employee.getEmail());
        if (existing.isPresent()) {
            return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
        }
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        employee.setStatus("ACTIVE");
        Employee saved = employeeRepository.save(employee);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }


    @Override
    public ResponseEntity<?> login(String email, String password) {
        Optional<Employee> optional = employeeRepository.findByEmail(email);
        if (optional.isEmpty()) {
            return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }

        Employee employee = optional.get();
        if (!passwordEncoder.matches(password, employee.getPassword())) {
            return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }

        // ✅ Generate JWT token
        String token = jwtUtil.generateToken(
                org.springframework.security.core.userdetails.User
                        .withUsername(employee.getEmail())
                        .password(employee.getPassword())
                        .authorities(employee.getRole().name())
                        .build()
        );

        // ✅ Prepare response map
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("token", token);
        response.put("employee", Map.of(
                "id", employee.getEmployeeId(),
                "name", employee.getName(),
                "email", employee.getEmail(),
                "role", employee.getRole(),
                "department", employee.getDepartment(),
                "designation", employee.getDesignation()
        ));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PreAuthorize("hasAnyAuthority('ADMIN', 'HR')")
    @Override
    public ResponseEntity<?> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getEmployeeById(UUID id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            return new ResponseEntity<>(employee.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Employee not found", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<?> updateEmployee(UUID id, Employee updatedEmployee) {
        Optional<Employee> optional = employeeRepository.findById(id);
        if (optional.isPresent()) {
            Employee employee = optional.get();
            employee.setName(updatedEmployee.getName());
            employee.setDepartment(updatedEmployee.getDepartment());
            employee.setDesignation(updatedEmployee.getDesignation());
            employee.setSkills(updatedEmployee.getSkills());
            employee.setKpi(updatedEmployee.getKpi());
            employee.setStatus(updatedEmployee.getStatus());
            employee.setManagerId(updatedEmployee.getManagerId());

            Employee updated = employeeRepository.save(employee);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Employee not found", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<?> deleteEmployee(UUID id) {
        Optional<Employee> optional = employeeRepository.findById(id);
        if (optional.isPresent()) {
            employeeRepository.delete(optional.get());
            return new ResponseEntity<>("Employee deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Employee not found", HttpStatus.NOT_FOUND);
        }
    }





//    @Override
//    public ResponseEntity<?> registerEmployee(Employee employee) {
//        if (employeeRepository.findByEmail(employee.getEmail()).isPresent()) {
//            return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
//        }
//        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
//        employee.setStatus("ACTIVE");
//        return new ResponseEntity<>(employeeRepository.save(employee), HttpStatus.CREATED);
//    }

//    @Override
//    public ResponseEntity<?> login(String email, String password) {
//        Optional<Employee> optional = employeeRepository.findByEmail(email);
//        if (optional.isEmpty()) return new ResponseEntity<>("Invalid email", HttpStatus.UNAUTHORIZED);
//
//        Employee emp = optional.get();
//        if (!passwordEncoder.matches(password, emp.getPassword())) {
//            return new ResponseEntity<>("Invalid password", HttpStatus.UNAUTHORIZED);
//        }
//
//        String token = jwtUtil.generateToken(
//                org.springframework.security.core.userdetails.User.withUsername(emp.getEmail())
//                        .password(emp.getPassword()).authorities(emp.getRole().name()).build()
//        );
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("message", "Login successful");
//        response.put("token", token);
//        response.put("employee", emp);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

    @Override
    public ResponseEntity<?> logout(String email) {
        return new ResponseEntity<>("Logged out successfully (client should discard token)", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> forgotPassword(String email, String newPassword) {
        Optional<Employee> optional = employeeRepository.findByEmail(email);
        if (optional.isEmpty()) return new ResponseEntity<>("Email not found", HttpStatus.NOT_FOUND);

        Employee emp = optional.get();
        emp.setPassword(passwordEncoder.encode(newPassword));
        employeeRepository.save(emp);
        return new ResponseEntity<>("Password updated successfully", HttpStatus.OK);
    }

//    @Override
//    public ResponseEntity<?> getAllEmployees() {
//        return new ResponseEntity<>(employeeRepository.findAll(), HttpStatus.OK);
//    }

//    @Override
//    public ResponseEntity<?> getEmployeeById(UUID id) {
//        return employeeRepository.findById(id)
//                .map(emp -> new ResponseEntity<>(emp, HttpStatus.OK))
//                .orElse(new ResponseEntity<>("Employee not found", HttpStatus.NOT_FOUND));
//    }

//    @Override
//    public ResponseEntity<?> updateEmployee(UUID id, Employee updated) {
//        Optional<Employee> opt = employeeRepository.findById(id);
//        if (opt.isEmpty()) return new ResponseEntity<>("Employee not found", HttpStatus.NOT_FOUND);
//
//        Employee emp = opt.get();
//        emp.setName(updated.getName());
//        emp.setDepartment(updated.getDepartment());
//        emp.setDesignation(updated.getDesignation());
//        emp.setSkills(updated.getSkills());
//        emp.setKpi(updated.getKpi());
//        emp.setStatus(updated.getStatus());
//        return new ResponseEntity<>(employeeRepository.save(emp), HttpStatus.OK);
//    }

//    @Override
//    public ResponseEntity<?> deleteEmployee(UUID id) {
//        Optional<Employee> opt = employeeRepository.findById(id);
//        if (opt.isEmpty()) return new ResponseEntity<>("Employee not found", HttpStatus.NOT_FOUND);
//
//        employeeRepository.delete(opt.get());
//        return new ResponseEntity<>("Employee deleted", HttpStatus.OK);
//    }

    @Override
    public ResponseEntity<?> getEmployeesByStatus(String status) {
        return new ResponseEntity<>(employeeRepository.findByStatus(status), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getEmployeesByRole(String role) {
        try {
            Role r = Role.valueOf(role);
            return new ResponseEntity<>(employeeRepository.findByRole(r), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid role", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> assignManager(UUID empId, UUID managerId) {
        Optional<Employee> empOpt = employeeRepository.findById(empId);
        Optional<Employee> mgrOpt = employeeRepository.findById(managerId);

        if (empOpt.isEmpty() || mgrOpt.isEmpty()) return new ResponseEntity<>("Invalid IDs", HttpStatus.BAD_REQUEST);

        Employee emp = empOpt.get();
        emp.setManagerId(managerId);
        return new ResponseEntity<>(employeeRepository.save(emp), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getTeamMembers(UUID managerId) {
        return new ResponseEntity<>(employeeRepository.findByManagerId(managerId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> updateStatus(UUID id, String status) {
        Optional<Employee> empOpt = employeeRepository.findById(id);
        if (empOpt.isEmpty()) return new ResponseEntity<>("Employee not found", HttpStatus.NOT_FOUND);

        Employee emp = empOpt.get();
        emp.setStatus(status);
        return new ResponseEntity<>(employeeRepository.save(emp), HttpStatus.OK);
    }


    @Override
    public ResponseEntity<?> addEmployeeToPip(UUID employeeId) {
        Optional<Employee> empOpt = employeeRepository.findById(employeeId);
        if (empOpt.isEmpty()) return new ResponseEntity<>("Employee not found", HttpStatus.NOT_FOUND);

        Employee emp = empOpt.get();
        if ("UNDER_PIP".equalsIgnoreCase(emp.getStatus())) {
            return new ResponseEntity<>("Employee is already under PIP", HttpStatus.BAD_REQUEST);
        }

        emp.setStatus("UNDER_PIP");
        employeeRepository.save(emp);
        return new ResponseEntity<>("Employee added to PIP successfully", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> resetPassword(UUID employeeId, String oldPassword, String newPassword) {
        Optional<Employee> optional = employeeRepository.findById(employeeId);
        if (optional.isEmpty()) {
            return new ResponseEntity<>("Employee not found", HttpStatus.NOT_FOUND);
        }

        Employee employee = optional.get();

        // Verify old password matches
        if (!passwordEncoder.matches(oldPassword, employee.getPassword())) {
            return new ResponseEntity<>("Old password is incorrect", HttpStatus.UNAUTHORIZED);
        }

        // Encode and update new password
        employee.setPassword(passwordEncoder.encode(newPassword));
        employeeRepository.save(employee);

        return new ResponseEntity<>("Password reset successfully", HttpStatus.OK);
    }


    @Override
    public ResponseEntity<?> getPipStatus(UUID employeeId) {
        Optional<Employee> empOpt = employeeRepository.findById(employeeId);
        if (empOpt.isEmpty()) return new ResponseEntity<>("Employee not found", HttpStatus.NOT_FOUND);

        Map<String, String> res = new HashMap<>();
        res.put("pipStatus", empOpt.get().getStatus());
        return new ResponseEntity<>(res, HttpStatus.OK);
    }












}








































//@Service
//public class EmployeeServiceImpl implements EmployeeService {
//
//    @Autowired
//    private EmployeeRepository employeeRepository;
//
//    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
//        this.employeeRepository = employeeRepository;
//    }
//
//
//
//
//
//    @Override
//    public Employee createEmployee(Employee employee) {
//        employee.setJoiningDate(employee.getJoiningDate() != null ? employee.getJoiningDate() : java.time.LocalDateTime.now());
//        return employeeRepository.save(employee);
//    }
//
//    @Override
//    public Employee getEmployeeById(UUID id) {
//        return employeeRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + id));
//    }
//
//    @Override
//    public List<Employee> getAllEmployees() {
//        return employeeRepository.findAll();
//    }
//
//    @Override
//    public List<Employee> getEmployeesByRole(Role role) {
//        return employeeRepository.findByRole(role);
//    }
//
//    @Override
//    public Employee updateEmployee(UUID id, Employee updated) {
//        Employee existing = getEmployeeById(id);
//        existing.setName(updated.getName());
//        existing.setEmail(updated.getEmail());
//        existing.setPassword(updated.getPassword());
//        existing.setRole(updated.getRole());
//        existing.setDepartment(updated.getDepartment());
//        existing.setDesignation(updated.getDesignation());
//        existing.setSkills(updated.getSkills());
//        existing.setCurrentKRA(updated.getCurrentKRA());
//        existing.setKpi(updated.getKpi());
//        existing.setManagerId(updated.getManagerId());
//        existing.setPhotoUrl(updated.getPhotoUrl());
//        existing.setStatus(updated.getStatus());
//        return employeeRepository.save(existing);
//    }
//
//    @Override
//    public void deleteEmployee(UUID id) {
//        employeeRepository.deleteById(id);
//    }
//}

package com.example.PipReviewSystem.controller;

import com.example.PipReviewSystem.entity.Employee;
import com.example.PipReviewSystem.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // --- DTOs for Request Bodies  ---

    // For /forgot-password/request-link
    public static class RequestResetLink {
        public String email;
        public String baseUrl;
    }

    // For /forgot-password/reset-with-token
    public static class ResetPasswordWithTokenRequest {
        public String token;
        public String newPassword;
    }

    // --- Endpoints ---

    @Operation(
            summary = "Step 1️⃣ - Register New Employee",
            description = "Allows ADMIN or HR to create a new employee account. A system-generated temporary password is emailed to the new employee."
    )
    @PreAuthorize("hasAnyAuthority('ADMIN', 'HR')")
    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody Employee employee,
                                      Authentication authentication) {
        String creatorRole = authentication.getAuthorities().iterator().next().getAuthority();
        return employeeService.registerEmployee(employee, creatorRole);
    }


    // New endpoint to request OTP for password reset
    @PostMapping("/reset-password/request-otp")
    public ResponseEntity<?> requestOtpForForgotPassword(@RequestParam String email) {
        return employeeService.requestPasswordResetOtp(email);
    }

    // New endpoint to verify OTP and reset password
    @PutMapping("/reset-password/verify-otp-reset")
    public ResponseEntity<?> verifyOtpAndResetPassword(@RequestParam String email,
                                                       @RequestParam String otp,
                                                       @RequestParam String newPassword) {
        return employeeService.verifyOtpAndResetPassword(email, otp, newPassword);
    }

    // --- NEW: Endpoint to request password reset link ---
    // This endpoint is public (no @PreAuthorize) as it's for forgotten passwords.
    @PostMapping("/reset-password/request-link")
    public ResponseEntity<?> requestPasswordResetLink(@RequestBody RequestResetLink request) {
        // The baseUrl is critical for the frontend to construct the clickable link.
        return employeeService.requestPasswordResetLink(request.email, request.baseUrl);
    }

    // --- NEW: Endpoint to reset password using the token from the link ---
    // This endpoint is public (no @PreAuthorize) as it's for forgotten passwords.
    @PutMapping("/reset-password/reset-with-token")
    public ResponseEntity<?> resetPasswordWithToken(@RequestBody ResetPasswordWithTokenRequest request) {
        return employeeService.resetPasswordWithToken(request.token, request.newPassword);
    }


    @PreAuthorize("hasAnyAuthority('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam UUID employeeId,
                                           @RequestParam String oldPassword,
                                           @RequestParam String newPassword) {
        return employeeService.resetPassword(employeeId, oldPassword, newPassword);
    }



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        return employeeService.login(email, password);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        return employeeService.getAllEmployees();
    }


    @PreAuthorize("hasAnyAuthority('ADMIN', 'HR')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        return employeeService.getEmployeeById(id);
    }


    @PreAuthorize("hasAnyAuthority('ADMIN', 'HR')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody Employee employee) {
        return employeeService.updateEmployee(id, employee);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'HR')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        return employeeService.deleteEmployee(id);
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam String email) {
        return employeeService.logout(email);
    }


@PreAuthorize("hasAnyAuthority('ADMIN', 'HR')")
@GetMapping("/status/{status}")
public ResponseEntity<?> byStatus(@PathVariable String status) {
    return employeeService.getEmployeesByStatus(status);
}


@PreAuthorize("hasAnyAuthority('ADMIN', 'HR')")
@GetMapping("/role/{role}")
public ResponseEntity<?> byRole(@PathVariable String role) {
    return employeeService.getEmployeesByRole(role);
}

@PreAuthorize("hasAnyAuthority('ADMIN', 'HR', 'MANAGER')")
@PutMapping("/assign-manager")
public ResponseEntity<?> assignManager(@RequestParam UUID employeeId, @RequestParam UUID managerId) {
    return employeeService.assignManager(employeeId, managerId);
}

@PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
@GetMapping("/team/{managerId}")
public ResponseEntity<?> getTeam(@PathVariable UUID managerId) {
    return employeeService.getTeamMembers(managerId);
}

@PutMapping("/update-status")
public ResponseEntity<?> updateStatus(@RequestParam UUID id, @RequestParam String status) {
    return employeeService.updateStatus(id, status);
}

@PreAuthorize("hasAnyAuthority('MANAGER','ADMIN')")
@PutMapping("/add-to-pip/{employeeId}")
public ResponseEntity<?> addToPip(@PathVariable UUID employeeId) {
    return employeeService.addEmployeeToPip(employeeId);
}

@PreAuthorize("hasAnyAuthority('ADMIN', 'HR', 'MANAGER')")
@GetMapping("/pip-status/{employeeId}")
public ResponseEntity<?> pipStatus(@PathVariable UUID employeeId) {
    return employeeService.getPipStatus(employeeId);
}
}



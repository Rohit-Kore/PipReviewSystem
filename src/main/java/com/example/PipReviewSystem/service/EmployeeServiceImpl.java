package com.example.PipReviewSystem.service;

import com.example.PipReviewSystem.config.JwtUtil;
import com.example.PipReviewSystem.dto.LoginResponseDTO;
import com.example.PipReviewSystem.entity.Employee;
import com.example.PipReviewSystem.enums.Role;
import com.example.PipReviewSystem.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MailService mailService;

    @Autowired
    private NotificationService notificationService;

    private static final long OTP_VALID_DURATION_MINUTES = 10;
    private static final long TEMPORARY_PASSWORD_VALID_DURATION_HOURS = 10;

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    private String generateRandomPassword() {
        String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
        String CHAR_UPPER = CHAR_LOWER.toUpperCase();
        String NUMBER = "0123456789";
        String OTHER_CHAR = "!@#$%&*()_+-=[]?";

        String PASSWORD_CHARS = CHAR_LOWER + CHAR_UPPER + NUMBER + OTHER_CHAR;
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(12);

        password.append(CHAR_LOWER.charAt(random.nextInt(CHAR_LOWER.length())));
        password.append(CHAR_UPPER.charAt(random.nextInt(CHAR_UPPER.length())));
        password.append(NUMBER.charAt(random.nextInt(NUMBER.length())));
        password.append(OTHER_CHAR.charAt(random.nextInt(OTHER_CHAR.length())));

        for (int i = 0; i < 8; i++) {
            password.append(PASSWORD_CHARS.charAt(random.nextInt(PASSWORD_CHARS.length())));
        }

        List<Character> pwdChars = new ArrayList<>();
        for (char c : password.toString().toCharArray()) {
            pwdChars.add(c);
        }
        Collections.shuffle(pwdChars);
        StringBuilder finalPassword = new StringBuilder(12);
        for (char c : pwdChars) {
            finalPassword.append(c);
        }
        return finalPassword.toString();
    }

    @Override
    public ResponseEntity<?> registerEmployee(Employee employee, String creatorRole) {
        Optional<Employee> existing = employeeRepository.findByEmail(employee.getEmail());
        if (existing.isPresent()) {
            return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
        }

        if (Role.ADMIN.name().equalsIgnoreCase(employee.getRole().name())) {
            return new ResponseEntity<>("Creating an ADMIN is not allowed through this API.", HttpStatus.FORBIDDEN);
        }

        String temporaryPassword = generateRandomPassword();
        employee.setPassword(passwordEncoder.encode(temporaryPassword));
        employee.setStatus("ACTIVE");
        employee.setTemporaryPassword(true);
        employee.setTemporaryPasswordGeneratedTime(LocalDateTime.now());

        Employee saved = employeeRepository.save(employee);

        notificationService.createNotification(saved, "Welcome to the system!", "Your account has been created. Please log in and change your password.", "INFO");

        List<Employee> admins = employeeRepository.findByRole(Role.ADMIN);
        for (Employee admin : admins) {
            notificationService.createNotification(admin, "New Employee Registered", saved.getName() + " (" + saved.getEmail() + ") has been successfully registered.", "INFO");
        }

        LocalDateTime expiryTime = saved.getTemporaryPasswordGeneratedTime().plusHours(TEMPORARY_PASSWORD_VALID_DURATION_HOURS);

        mailService.sendMail(saved.getEmail(), "Welcome to PIP Review System - Your Account Details",
                "Dear " + saved.getName() + ",\n\nWelcome to the PIP Review System!\n\n" +
                        "Your account has been successfully created.\n" +
                        "Your login email is: " + saved.getEmail() + "\n" +
                        "Your temporary password is: " + temporaryPassword + "\n\n" +
                        "This temporary password will expire in " + TEMPORARY_PASSWORD_VALID_DURATION_HOURS + " hours (on " +
                        expiryTime.toLocalDate() + " at " + expiryTime.toLocalTime().truncatedTo(ChronoUnit.MINUTES) + " IST).\n" +
                        "Please login and change your password immediately for security reasons.\n\n" +
                        "Regards,\nPIP Review System Team");

        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> forgotPassword(String email, String newPassword) {
        return new ResponseEntity<>("This method is deprecated. Please use the OTP-based password reset endpoints.", HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Override
    public ResponseEntity<LoginResponseDTO> login(String email, String password) {
        Optional<Employee> optional = employeeRepository.findByEmail(email);
        if (optional.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        Employee employee = optional.get();

        if (!passwordEncoder.matches(password, employee.getPassword())) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        if (employee.isTemporaryPassword() && employee.getTemporaryPasswordGeneratedTime() != null) {
            LocalDateTime expiryDateTime = employee.getTemporaryPasswordGeneratedTime().plusHours(TEMPORARY_PASSWORD_VALID_DURATION_HOURS);
            if (LocalDateTime.now().isAfter(expiryDateTime)) {
                employee.setStatus("INACTIVE");
                employee.setTemporaryPassword(false);
                employee.setTemporaryPasswordGeneratedTime(null);
                employeeRepository.save(employee);
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }
        }

        String token = jwtUtil.generateToken(
                org.springframework.security.core.userdetails.User
                        .withUsername(employee.getEmail())
                        .password(employee.getPassword())
                        .authorities(employee.getRole().name())
                        .build()
        );

        LoginResponseDTO response = new LoginResponseDTO();
        response.setId(employee.getEmployeeId());
        response.setName(employee.getName());
        response.setEmail(employee.getEmail());
        response.setRole(employee.getRole());
        response.setDepartment(employee.getDepartment());
        response.setDesignation(employee.getDesignation());
        response.setPasswordChangeRequired(employee.isTemporaryPassword());
        response.setToken(token);

        notificationService.createNotification(employee, "Login Successful", "You have successfully logged into the PIP Review System.", "INFO");

        List<Employee> admins = employeeRepository.findByRole(Role.ADMIN);
        for (Employee admin : admins) {
            notificationService.createNotification(admin, "User Logged In", employee.getName() + " (" + employee.getEmail() + ") has successfully logged in.", "INFO");
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

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
            return new ResponseEntity<>("Employee not found with ID: " + id, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<?> updateEmployee(UUID id, Employee updatedEmployee) {
        Optional<Employee> optional = employeeRepository.findById(id);
        if (optional.isPresent()) {
            Employee employee = optional.get();

            boolean changesMade = false;

            if (!Objects.equals(employee.getName(), updatedEmployee.getName())) { employee.setName(updatedEmployee.getName()); changesMade = true; }
            if (!Objects.equals(employee.getDepartment(), updatedEmployee.getDepartment())) { employee.setDepartment(updatedEmployee.getDepartment()); changesMade = true; }
            if (!Objects.equals(employee.getDesignation(), updatedEmployee.getDesignation())) { employee.setDesignation(updatedEmployee.getDesignation()); changesMade = true; }
            if (updatedEmployee.getSkills() != null && !Objects.equals(employee.getSkills(), updatedEmployee.getSkills())) { employee.setSkills(updatedEmployee.getSkills()); changesMade = true; }
            if (updatedEmployee.getKpi() != null && !Objects.equals(employee.getKpi(), updatedEmployee.getKpi())) { employee.setKpi(updatedEmployee.getKpi()); changesMade = true; }
            if (!Objects.equals(employee.getStatus(), updatedEmployee.getStatus())) { employee.setStatus(updatedEmployee.getStatus()); changesMade = true; }

            if (updatedEmployee.getManager() != null && (employee.getManager() == null || !Objects.equals(employee.getManager().getEmployeeId(), updatedEmployee.getManager().getEmployeeId()))) {
                employee.setManager(updatedEmployee.getManager());
                changesMade = true;
            } else if (updatedEmployee.getManager() == null && employee.getManager() != null) {
                employee.setManager(null);
                changesMade = true;
            }

            Employee updated = employeeRepository.save(employee);

            if (changesMade) {
                notificationService.createNotification(updated, "Profile Updated", "Your employee profile has been updated. Please review the changes.", "INFO");
            }

            return new ResponseEntity<>(updated, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Employee not found with ID: " + id, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<?> deleteEmployee(UUID id) {
        Optional<Employee> optional = employeeRepository.findById(id);
        if (optional.isPresent()) {
            Employee employeeToDelete = optional.get();

            List<Employee> admins = employeeRepository.findByRole(Role.ADMIN);
            for (Employee admin : admins) {
                notificationService.createNotification(admin, "Employee Deleted", employeeToDelete.getName() + " (" + employeeToDelete.getEmail() + ") has been deleted from the system.", "ALERT");
            }

            employeeRepository.delete(employeeToDelete);
            return new ResponseEntity<>("Employee deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Employee not found with ID: " + id, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<?> logout(String email) {
        Optional<Employee> optional = employeeRepository.findByEmail(email);
        if (optional.isPresent()) {
            Employee employee = optional.get();

            notificationService.createNotification(employee, "Logout Successful", "You have successfully logged out of the PIP Review System.", "INFO");

            List<Employee> admins = employeeRepository.findByRole(Role.ADMIN);
            for (Employee admin : admins) {
                notificationService.createNotification(admin, "User Logged Out", employee.getName() + " (" + employee.getEmail() + ") has logged out.", "INFO");
            }
        }
        return new ResponseEntity<>("Logged out successfully (client should discard token)", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> requestPasswordResetOtp(String email) {
        Optional<Employee> optional = employeeRepository.findByEmail(email);
        if (optional.isEmpty()) {
            return new ResponseEntity<>("Email not found.", HttpStatus.NOT_FOUND);
        }

        Employee employee = optional.get();
        String otp = generateOtp();
        employee.setOtp(otp);
        employee.setOtpGeneratedTime(LocalDateTime.now());

        employeeRepository.save(employee);
        notificationService.createNotification(employee, "Password Reset OTP Sent", "An OTP has been sent to your email for password reset. It's valid for " + OTP_VALID_DURATION_MINUTES + " minutes.", "INFO");

        mailService.sendMail(email, "Password Reset OTP for PIP Review System",
                "Dear " + employee.getName() + ",\n\n" +
                        "Your One-Time Password (OTP) for password reset is: " + otp + "\n\n" +
                        "This OTP is valid for " + OTP_VALID_DURATION_MINUTES + " minutes.\n" +
                        "If you did not request this, please ignore this email.\n\n" +
                        "Regards,\nPIP Review System Team");

        return new ResponseEntity<>("OTP sent to your email.", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> verifyOtpAndResetPassword(String email, String otp, String newPassword) {
        Optional<Employee> optional = employeeRepository.findByEmail(email);
        if (optional.isEmpty()) {
            return new ResponseEntity<>("Employee not found.", HttpStatus.NOT_FOUND);
        }

        Employee employee = optional.get();

        if (employee.getOtp() == null || !employee.getOtp().equals(otp)) {
            return new ResponseEntity<>("Invalid OTP.", HttpStatus.BAD_REQUEST);
        }

        if (employee.getOtpGeneratedTime() == null ||
                LocalDateTime.now().isAfter(employee.getOtpGeneratedTime().plusMinutes(OTP_VALID_DURATION_MINUTES))) {
            employee.setOtp(null);
            employee.setOtpGeneratedTime(null);
            employeeRepository.save(employee);
            return new ResponseEntity<>("OTP expired.", HttpStatus.BAD_REQUEST);
        }

        employee.setPassword(passwordEncoder.encode(newPassword));
        employee.setOtp(null);
        employee.setOtpGeneratedTime(null);

        employee.setTemporaryPassword(false);
        employee.setTemporaryPasswordGeneratedTime(null);

        employeeRepository.save(employee);

        notificationService.createNotification(employee, "Password Reset Successful", "Your password has been successfully reset.", "INFO");

        mailService.sendMail(email, "Password Changed Successfully",
                "Dear " + employee.getName() + ",\n\n" +
                        "Your password for PIP Review System has been successfully changed.\n\n" +
                        "If you did not make this change, please contact support immediately.\n\n" +
                        "Regards,\nPIP Review System Team");

        return new ResponseEntity<>("Password reset successfully.", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> requestPasswordResetLink(String email, String baseUrl) {
        Optional<Employee> optional = employeeRepository.findByEmail(email);
        if (optional.isEmpty()) {
            return new ResponseEntity<>("Email not found.", HttpStatus.NOT_FOUND);
        }

        Employee employee = optional.get();
        String resetToken = UUID.randomUUID().toString();
        employee.setPasswordResetToken(resetToken);
        employee.setPasswordResetTokenExpiryTime(LocalDateTime.now().plusHours(1));
        employeeRepository.save(employee);

        notificationService.createNotification(employee, "Password Reset Link Sent", "A password reset link has been sent to your email. It's valid for 1 hour.", "INFO");

        String resetLink = baseUrl + "/reset-password?token=" + resetToken;

        mailService.sendMail(employee.getEmail(), "Password Reset Link for PIP Review System",
                "Dear " + employee.getName() + ",\n\n" +
                        "You have requested to reset your password. Please click on the link below to reset your password:\n\n" +
                        resetLink + "\n\n" +
                        "This link is valid for 1 hour.\n" +
                        "If you did not request this, please ignore this email.\n\n" +
                        "Regards,\nPIP Review System Team");

        return new ResponseEntity<>("Password reset link sent to your email.", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> resetPasswordWithToken(String token, String newPassword) {
        Optional<Employee> optional = employeeRepository.findByPasswordResetToken(token);
        if (optional.isEmpty()) {
            return new ResponseEntity<>("Invalid or expired password reset token.", HttpStatus.BAD_REQUEST);
        }
        Employee employee = optional.get();
        if (employee.getPasswordResetTokenExpiryTime() == null ||
                LocalDateTime.now().isAfter(employee.getPasswordResetTokenExpiryTime())) {
            employee.setPasswordResetToken(null);
            employee.setPasswordResetTokenExpiryTime(null);
            employeeRepository.save(employee);
            return new ResponseEntity<>("Password reset token expired.", HttpStatus.BAD_REQUEST);
        }
        employee.setPassword(passwordEncoder.encode(newPassword));
        employee.setPasswordResetToken(null);
        employee.setPasswordResetTokenExpiryTime(null);
        employee.setTemporaryPassword(false);
        employee.setTemporaryPasswordGeneratedTime(null);

        employeeRepository.save(employee);

        notificationService.createNotification(employee, "Password Reset Successful", "Your password has been successfully reset.", "INFO");

        mailService.sendMail(employee.getEmail(), "Password Changed Successfully",
                "Dear " + employee.getName() + ",\n\n" +
                        "Your password for PIP Review System has been successfully changed.\n\n" +
                        "If you did not make this change, please contact support immediately.\n\n" +
                        "Regards,\nPIP Review System Team");
        return new ResponseEntity<>("Password reset successfully.", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getEmployeesByStatus(String status) {
        return new ResponseEntity<>(employeeRepository.findByStatus(status), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getEmployeesByRole(String role) {
        try {
            Role r = Role.valueOf(role.toUpperCase());
            return new ResponseEntity<>(employeeRepository.findByRole(r), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid role: " + role, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> assignManager(UUID empId, UUID managerId) {
        Optional<Employee> empOpt = employeeRepository.findById(empId);
        Optional<Employee> mgrOpt = employeeRepository.findById(managerId);

        if (empOpt.isEmpty()) {
            return new ResponseEntity<>("Employee not found with ID: " + empId, HttpStatus.NOT_FOUND);
        }
        if (mgrOpt.isEmpty()) {
            return new ResponseEntity<>("Manager not found with ID: " + managerId, HttpStatus.NOT_FOUND);
        }
        if (!mgrOpt.get().getRole().equals(Role.MANAGER) && !mgrOpt.get().getRole().equals(Role.ADMIN)) {
            return new ResponseEntity<>("Assigned manager must have a 'MANAGER' or 'ADMIN' role.", HttpStatus.BAD_REQUEST);
        }

        Employee emp = empOpt.get();
        emp.setManager(mgrOpt.get());
        Employee updatedEmployee = employeeRepository.save(emp);

        notificationService.createNotification(updatedEmployee, "New Manager Assigned", "You have been assigned a new manager: " + updatedEmployee.getManager().getName() + ".", "INFO");

        notificationService.createNotification(updatedEmployee.getManager(), "New Team Member Assigned", "You have been assigned a new team member: " + updatedEmployee.getName() + ".", "INFO");

        return new ResponseEntity<>(updatedEmployee, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getTeamMembers(UUID managerId) {
        Optional<Employee> mgrOpt = employeeRepository.findById(managerId);
        if (mgrOpt.isEmpty()) {
            return new ResponseEntity<>("Manager not found with ID: " + managerId, HttpStatus.NOT_FOUND);
        }

        List<Employee> teamMembers = employeeRepository.findByManager_EmployeeId(managerId);
        return new ResponseEntity<>(teamMembers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> updateStatus(UUID id, String status) {
        Optional<Employee> empOpt = employeeRepository.findById(id);
        if (empOpt.isEmpty()) {
            return new ResponseEntity<>("Employee not found with ID: " + id, HttpStatus.NOT_FOUND);
        }

        Employee emp = empOpt.get();
        emp.setStatus(status);
        Employee updatedEmp = employeeRepository.save(emp);

        notificationService.createNotification(updatedEmp, "Your Status Updated", "Your status has been updated to: " + updatedEmp.getStatus() + ".", "INFO");

        List<Employee> admins = employeeRepository.findByRole(Role.ADMIN);
        for (Employee admin : admins) {
            notificationService.createNotification(admin, "Employee Status Changed", updatedEmp.getName() + "'s status has been changed to " + updatedEmp.getStatus() + ".", "INFO");
        }
        return new ResponseEntity<>(updatedEmp, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> addEmployeeToPip(UUID employeeId) {
        Optional<Employee> empOpt = employeeRepository.findById(employeeId);
        if (empOpt.isEmpty()) {
            return new ResponseEntity<>("Employee not found with ID: " + employeeId, HttpStatus.NOT_FOUND);
        }

        Employee emp = empOpt.get();
        if ("UNDER_PIP".equalsIgnoreCase(emp.getStatus())) {
            return new ResponseEntity<>("Employee is already under PIP", HttpStatus.BAD_REQUEST);
        }

        emp.setStatus("UNDER_PIP");
        employeeRepository.save(emp);

        notificationService.createNotification(emp, "Performance Improvement Plan", "You have been placed on a Performance Improvement Plan. Please check your details.", "ALERT");

        if (emp.getManager() != null) {
            notificationService.createNotification(emp.getManager(), "PIP Alert", emp.getName() + " has been placed on a PIP. You have been notified.", "ALERT");
        }

        return new ResponseEntity<>("Employee added to PIP successfully", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> resetPassword(UUID employeeId, String oldPassword, String newPassword) {
        Optional<Employee> optional = employeeRepository.findById(employeeId);
        if (optional.isEmpty()) {
            return new ResponseEntity<>("Employee not found with ID: " + employeeId, HttpStatus.NOT_FOUND);
        }

        Employee employee = optional.get();

        if (!passwordEncoder.matches(oldPassword, employee.getPassword())) {
            return new ResponseEntity<>("Old password is incorrect", HttpStatus.UNAUTHORIZED);
        }

        employee.setPassword(passwordEncoder.encode(newPassword));
        employee.setTemporaryPassword(false);
        employee.setTemporaryPasswordGeneratedTime(null);

        employeeRepository.save(employee);

        notificationService.createNotification(employee, "Password Reset Successful", "Your password has been successfully reset.", "INFO");

        String to = employee.getEmail();
        String subject = "Password Reset Successful";
        String message = "Hi " + employee.getName() + ",\n\nYour password has been successfully reset.\n\n" +
                "If you did not request this change, please contact the admin immediately.\n\n" +
                "Regards,\nPIP Review System Team";

        mailService.sendMail(to, subject, message);

        return new ResponseEntity<>("Password reset successfully", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getPipStatus(UUID employeeId) {
        Optional<Employee> empOpt = employeeRepository.findById(employeeId);
        if (empOpt.isEmpty()) {
            return new ResponseEntity<>("Employee not found with ID: " + employeeId, HttpStatus.NOT_FOUND);
        }

        Map<String, String> res = new HashMap<>();
        res.put("pipStatus", empOpt.get().getStatus());
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Override
    public List<Employee> getAssignedEmployees(String managerEmail) {
        return employeeRepository.findByManager_Email(managerEmail);
    }
}

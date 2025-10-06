package com.example.PipReviewSystem.service;

import com.example.PipReviewSystem.config.JwtUtil;
import com.example.PipReviewSystem.dto.LoginResponseDTO; // Added: For structured login response
import com.example.PipReviewSystem.entity.Employee;
import com.example.PipReviewSystem.enums.Role;
import com.example.PipReviewSystem.repository.EmployeeRepository;
import com.example.PipReviewSystem.repository.PipRepository; // Make sure this is imported if used
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors; // Added: For stream operations like in getEmployeesByRole

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

    @Autowired // Added: For sending real-time notifications
    private NotificationService notificationService;

    // --- Constants ---
    private static final long OTP_VALID_DURATION_MINUTES = 10; // minutes
    private static final long TEMPORARY_PASSWORD_VALID_DURATION_HOURS = 10; // hours

    /**
     * Generates a 6-digit One-Time Password (OTP).
     * @return A string representation of the OTP.
     */
    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generates a number between 100000 and 999999
        return String.valueOf(otp);
    }

    /**
     * Generates a strong, random 12-character password.
     * Ensures inclusion of lowercase, uppercase, numbers, and special characters.
     * @return A randomly generated password.
     */
    private String generateRandomPassword() {
        String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
        String CHAR_UPPER = CHAR_LOWER.toUpperCase();
        String NUMBER = "0123456789";
        String OTHER_CHAR = "!@#$%&*()_+-=[]?";

        String PASSWORD_CHARS = CHAR_LOWER + CHAR_UPPER + NUMBER + OTHER_CHAR;
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(12); // Generate a 12-character password

        // Ensure at least one of each type
        password.append(CHAR_LOWER.charAt(random.nextInt(CHAR_LOWER.length())));
        password.append(CHAR_UPPER.charAt(random.nextInt(CHAR_UPPER.length())));
        password.append(NUMBER.charAt(random.nextInt(NUMBER.length())));
        password.append(OTHER_CHAR.charAt(random.nextInt(OTHER_CHAR.length())));

        for (int i = 0; i < 8; i++) { // 12 - 4 (guaranteed chars) = 8 remaining
            password.append(PASSWORD_CHARS.charAt(random.nextInt(PASSWORD_CHARS.length())));
        }

        // Shuffle the characters to ensure randomness in character positions
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


    /**
     * Registers a new employee in the system.
     * Generates a temporary password, encrypts it, sets temporary password flags,
     * and sends a welcome email with login details and password expiry.
     * ADMIN role creation is restricted through this API.
     *
     * @param employee The employee object containing details.
     * @param creatorRole The role of the user creating this employee (e.g., "ADMIN", "HR").
     * @return ResponseEntity with the saved Employee object or an error message.
     */
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
        employee.setTemporaryPassword(true); // Mark as temporary password
        employee.setTemporaryPasswordGeneratedTime(LocalDateTime.now()); // Set generation time

        Employee saved = employeeRepository.save(employee);

        // Notification for the newly registered employee
        String notificationTitle = "Welcome to the system!";
        String notificationMessage = "Your account has been created. Please log in and change your password.";
        notificationService.createNotification(saved, notificationTitle, notificationMessage, "INFO"); // Added notification

        // Notification for Admins about new registration (ADDED)
        List<Employee> admins = employeeRepository.findByRole(Role.ADMIN);
        for (Employee admin : admins) {
            String adminNotificationTitle = "New Employee Registered";
            String adminNotificationMessage = saved.getName() + " (" + saved.getEmail() + ") has been successfully registered.";
            notificationService.createNotification(admin, adminNotificationTitle, adminNotificationMessage, "INFO");
        }


        // Calculate expiry time for the email message
        LocalDateTime expiryTime = saved.getTemporaryPasswordGeneratedTime().plusHours(TEMPORARY_PASSWORD_VALID_DURATION_HOURS);

        // Send welcome email with credentials and expiry
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

    /**
     * Deprecated method for direct password reset without OTP/token.
     * Clients should use OTP-based or link-based password reset.
     *
     * @param email The employee's email.
     * @param newPassword The new password.
     * @return ResponseEntity indicating method not allowed.
     */
    @Override
    public ResponseEntity<?> forgotPassword(String email, String newPassword) {
        // This method is now deprecated in favor of the OTP-based reset
        return new ResponseEntity<>("This method is deprecated. Please use the OTP-based password reset endpoints.", HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Authenticates an employee and issues a JWT token.
     * Checks for temporary password expiry and indicates if a password change is required.
     *
     * @param email The employee's email.
     * @param password The employee's password.
     * @return ResponseEntity with login success message, JWT token, employee details, and password change requirement.
     */
    @Override
    public ResponseEntity<LoginResponseDTO> login(String email, String password) { // Changed return type to LoginResponseDTO
        Optional<Employee> optional = employeeRepository.findByEmail(email);
        if (optional.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); // Modified for LoginResponseDTO
        }

        Employee employee = optional.get();

        if (!passwordEncoder.matches(password, employee.getPassword())) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); // Modified for LoginResponseDTO
        }

        // Temporary password expiry check
        if (employee.isTemporaryPassword() && employee.getTemporaryPasswordGeneratedTime() != null) {
            LocalDateTime expiryDateTime = employee.getTemporaryPasswordGeneratedTime().plusHours(TEMPORARY_PASSWORD_VALID_DURATION_HOURS);
            if (LocalDateTime.now().isAfter(expiryDateTime)) {
                // Temporary password has expired
                employee.setStatus("INACTIVE"); // Or a specific status like "TEMP_PASSWORD_EXPIRED"
                employee.setTemporaryPassword(false); // Clear temporary flag
                employee.setTemporaryPasswordGeneratedTime(null);
                employeeRepository.save(employee);
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); // Modified for LoginResponseDTO
            }
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(
                org.springframework.security.core.userdetails.User
                        .withUsername(employee.getEmail())
                        .password(employee.getPassword())
                        .authorities(employee.getRole().name())
                        .build()
        );

        // Prepare response using LoginResponseDTO (MODIFIED)
        LoginResponseDTO response = new LoginResponseDTO();
        response.setId(employee.getEmployeeId());
        response.setName(employee.getName());
        response.setEmail(employee.getEmail());
        response.setRole(employee.getRole());
        response.setDepartment(employee.getDepartment());
        response.setDesignation(employee.getDesignation());
        response.setPasswordChangeRequired(employee.isTemporaryPassword());
        response.setToken(token); // Added: Setting the JWT token

        // New notification call: On successful login (ADDED)
        String notificationTitle = "Login Successful";
        String notificationMessage = "You have successfully logged into the PIP Review System.";
        notificationService.createNotification(employee, notificationTitle, notificationMessage, "INFO");

        // Send login notification to ADMINs (ADDED)
        List<Employee> admins = employeeRepository.findByRole(Role.ADMIN);
        for (Employee admin : admins) {
            String adminNotificationTitle = "User Logged In";
            String adminNotificationMessage = employee.getName() + " (" + employee.getEmail() + ") has successfully logged in.";
            notificationService.createNotification(admin, adminNotificationTitle, adminNotificationMessage, "INFO");
        }


        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves a list of all employees.
     * This method is secured at the controller level (e.g., using @PreAuthorize).
     *
     * @return ResponseEntity with a list of Employee objects.
     */
    @Override
    public ResponseEntity<?> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    /**
     * Retrieves an employee by their unique ID.
     *
     * @param id The UUID of the employee.
     * @return ResponseEntity with the Employee object or a NOT_FOUND status.
     */
    @Override
    public ResponseEntity<?> getEmployeeById(UUID id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            return new ResponseEntity<>(employee.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Employee not found with ID: " + id, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Updates an existing employee's details (excluding password).
     * Password changes should be handled via dedicated methods like resetPassword.
     *
     * @param id The UUID of the employee to update.
     * @param updatedEmployee An Employee object with updated details.
     * @return ResponseEntity with the updated Employee object or a NOT_FOUND status.
     */
    @Override
    public ResponseEntity<?> updateEmployee(UUID id, Employee updatedEmployee) {
        Optional<Employee> optional = employeeRepository.findById(id);
        if (optional.isPresent()) {
            Employee employee = optional.get();

            // Flag to check if any significant changes were made (ADDED)
            boolean changesMade = false;

            // Update fields and check if changes were made (MODIFIED)
            if (!Objects.equals(employee.getName(), updatedEmployee.getName())) { employee.setName(updatedEmployee.getName()); changesMade = true; }
            if (!Objects.equals(employee.getDepartment(), updatedEmployee.getDepartment())) { employee.setDepartment(updatedEmployee.getDepartment()); changesMade = true; }
            if (!Objects.equals(employee.getDesignation(), updatedEmployee.getDesignation())) { employee.setDesignation(updatedEmployee.getDesignation()); changesMade = true; }
            if (updatedEmployee.getSkills() != null && !Objects.equals(employee.getSkills(), updatedEmployee.getSkills())) { employee.setSkills(updatedEmployee.getSkills()); changesMade = true; }
            if (updatedEmployee.getKpi() != null && !Objects.equals(employee.getKpi(), updatedEmployee.getKpi())) { employee.setKpi(updatedEmployee.getKpi()); changesMade = true; }
            if (!Objects.equals(employee.getStatus(), updatedEmployee.getStatus())) { employee.setStatus(updatedEmployee.getStatus()); changesMade = true; }

            // Check for manager change
            // Assuming Employee entity has `@ManyToOne Employee manager;` field
            if (updatedEmployee.getManager() != null && (employee.getManager() == null || !Objects.equals(employee.getManager().getEmployeeId(), updatedEmployee.getManager().getEmployeeId()))) {
                employee.setManager(updatedEmployee.getManager());
                changesMade = true;
            } else if (updatedEmployee.getManager() == null && employee.getManager() != null) { // Manager was set, now being unset
                employee.setManager(null);
                changesMade = true;
            }

            Employee updated = employeeRepository.save(employee);

            // Notification for the updated employee if changes were made (ADDED)
            if (changesMade) {
                String notificationTitle = "Profile Updated";
                String notificationMessage = "Your employee profile has been updated. Please review the changes.";
                notificationService.createNotification(updated, notificationTitle, notificationMessage, "INFO");
            }

            return new ResponseEntity<>(updated, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Employee not found with ID: " + id, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Deletes an employee by their unique ID.
     *
     * @param id The UUID of the employee to delete.
     * @return ResponseEntity with a success message or a NOT_FOUND status.
     */
    @Override
    public ResponseEntity<?> deleteEmployee(UUID id) {
        Optional<Employee> optional = employeeRepository.findById(id);
        if (optional.isPresent()) {
            Employee employeeToDelete = optional.get(); // Get employee details BEFORE deletion (ADDED)

            // Notification for Admins about employee deletion (ADDED)
            List<Employee> admins = employeeRepository.findByRole(Role.ADMIN);
            for (Employee admin : admins) {
                String adminNotificationTitle = "Employee Deleted";
                String adminNotificationMessage = employeeToDelete.getName() + " (" + employeeToDelete.getEmail() + ") has been deleted from the system.";
                notificationService.createNotification(admin, adminNotificationTitle, adminNotificationMessage, "ALERT");
            }

            // Note: Sending a WebSocket notification to the deleted employee
            // won't work if they are logged in and their session is about to be terminated.
            // For a deleted user, an email might be a more reliable notification method.
            // If you still want to attempt, it would be here:
            // String userNotificationTitle = "Account Deleted";
            // String userNotificationMessage = "Your account has been deleted from the PIP Review System.";
            // notificationService.createNotification(employeeToDelete, userNotificationTitle, userNotificationMessage, "ALERT");

            employeeRepository.delete(employeeToDelete); // Perform deletion after getting details and sending notifications
            return new ResponseEntity<>("Employee deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Employee not found with ID: " + id, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Logs out an employee.
     * Note: For JWT, logout primarily involves client-side token discarding.
     *
     * @param email The email of the employee logging out.
     * @return ResponseEntity with a success message.
     */
    @Override
    public ResponseEntity<?> logout(String email) {
        Optional<Employee> optional = employeeRepository.findByEmail(email); // Added: To fetch employee details for notifications
        if (optional.isPresent()) {
            Employee employee = optional.get();

            // Notification for the employee who logged out (ADDED)
            String notificationTitle = "Logout Successful";
            String notificationMessage = "You have successfully logged out of the PIP Review System.";
            notificationService.createNotification(employee, notificationTitle, notificationMessage, "INFO");

            // Notification for Admins about logout (optional, depending on requirement) (ADDED)
            List<Employee> admins = employeeRepository.findByRole(Role.ADMIN);
            for (Employee admin : admins) {
                String adminNotificationTitle = "User Logged Out";
                String adminNotificationMessage = employee.getName() + " (" + employee.getEmail() + ") has logged out.";
                notificationService.createNotification(admin, adminNotificationTitle, adminNotificationMessage, "INFO");
            }
        }
        return new ResponseEntity<>("Logged out successfully (client should discard token)", HttpStatus.OK);
    }

    /**
     * Initiates the password reset process by sending an OTP to the employee's email.
     *
     * @param email The employee's email.
     * @return ResponseEntity with a success message or NOT_FOUND if email not found.
     */
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
        // Notification: OTP sent (ADDED)
        String notificationTitle = "Password Reset OTP Sent";
        String notificationMessage = "An OTP has been sent to your email for password reset. It's valid for " + OTP_VALID_DURATION_MINUTES + " minutes.";
        notificationService.createNotification(employee, notificationTitle, notificationMessage, "INFO");


        mailService.sendMail(email, "Password Reset OTP for PIP Review System",
                "Dear " + employee.getName() + ",\n\n" +
                        "Your One-Time Password (OTP) for password reset is: " + otp + "\n\n" +
                        "This OTP is valid for " + OTP_VALID_DURATION_MINUTES + " minutes.\n" +
                        "If you did not request this, please ignore this email.\n\n" +
                        "Regards,\nPIP Review System Team");

        return new ResponseEntity<>("OTP sent to your email.", HttpStatus.OK);
    }

    /**
     * Verifies the provided OTP and resets the employee's password.
     * Clears OTP and temporary password flags upon successful reset.
     *
     * @param email The employee's email.
     * @param otp The OTP received by the employee.
     * @param newPassword The new password to set.
     * @return ResponseEntity with a success message or an error message (invalid/expired OTP, employee not found).
     */
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
            // Clear OTP fields after expiry or invalid attempt
            employee.setOtp(null);
            employee.setOtpGeneratedTime(null);
            employeeRepository.save(employee);
            return new ResponseEntity<>("OTP expired.", HttpStatus.BAD_REQUEST);
        }

        // OTP is valid, reset password
        employee.setPassword(passwordEncoder.encode(newPassword));
        employee.setOtp(null); // Clear OTP after successful reset
        employee.setOtpGeneratedTime(null);

        // Clear temporary password flags if reset via OTP
        employee.setTemporaryPassword(false);
        employee.setTemporaryPasswordGeneratedTime(null);

        employeeRepository.save(employee);

        // Notification: Password reset successful (ADDED)
        String notificationTitle = "Password Reset Successful";
        String notificationMessage = "Your password has been successfully reset.";
        notificationService.createNotification(employee, notificationTitle, notificationMessage, "INFO");

        mailService.sendMail(email, "Password Changed Successfully",
                "Dear " + employee.getName() + ",\n\n" +
                        "Your password for PIP Review System has been successfully changed.\n\n" +
                        "If you did not make this change, please contact support immediately.\n\n" +
                        "Regards,\nPIP Review System Team");

        return new ResponseEntity<>("Password reset successfully.", HttpStatus.OK);
    }

    /**
     * Initiates a password reset process by sending a reset link to the employee's email.
     * Requires `passwordResetToken` and `passwordResetTokenExpiryTime` fields in Employee entity.
     *
     * @param email The employee's email.
     * @param baseUrl The base URL for the password reset link (e.g., frontend URL).
     * @return ResponseEntity with a success message or NOT_FOUND if email not found.
     */
    @Override
    public ResponseEntity<?> requestPasswordResetLink(String email, String baseUrl) {
        Optional<Employee> optional = employeeRepository.findByEmail(email);
        if (optional.isEmpty()) {
            return new ResponseEntity<>("Email not found.", HttpStatus.NOT_FOUND);
        }

        Employee employee = optional.get();
        String resetToken = UUID.randomUUID().toString();
        employee.setPasswordResetToken(resetToken);
        employee.setPasswordResetTokenExpiryTime(LocalDateTime.now().plusHours(1)); // Token valid for 1 hour
        employeeRepository.save(employee);

        // Notification: Password reset link sent (ADDED)
        String notificationTitle = "Password Reset Link Sent";
        String notificationMessage = "A password reset link has been sent to your email. It's valid for 1 hour.";
        notificationService.createNotification(employee, notificationTitle, notificationMessage, "INFO");

        String resetLink = baseUrl + "/reset-password?token=" + resetToken; // Adjust this URL based on your frontend route

        mailService.sendMail(employee.getEmail(), "Password Reset Link for PIP Review System",
                "Dear " + employee.getName() + ",\n\n" +
                        "You have requested to reset your password. Please click on the link below to reset your password:\n\n" +
                        resetLink + "\n\n" +
                        "This link is valid for 1 hour.\n" +
                        "If you did not request this, please ignore this email.\n\n" +
                        "Regards,\nPIP Review System Team");

        return new ResponseEntity<>("Password reset link sent to your email.", HttpStatus.OK);
    }

    /**
     * Resets the employee's password using a valid reset token.
     * Clears token and temporary password flags upon successful reset.
     *
     * @param token The password reset token.
     * @param newPassword The new password to set.
     * @return ResponseEntity with a success message or an error message (invalid/expired token).
     */
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
        // Clear temporary password flags if reset via token
        employee.setTemporaryPassword(false);
        employee.setTemporaryPasswordGeneratedTime(null);

        employeeRepository.save(employee);

        // Notification: Password reset successful via token (ADDED)
        String notificationTitle = "Password Reset Successful";
        String notificationMessage = "Your password has been successfully reset.";
        notificationService.createNotification(employee, notificationTitle, notificationMessage, "INFO");

        mailService.sendMail(employee.getEmail(), "Password Changed Successfully",
                "Dear " + employee.getName() + ",\n\n" +
                        "Your password for PIP Review System has been successfully changed.\n\n" +
                        "If you did not make this change, please contact support immediately.\n\n" +
                        "Regards,\nPIP Review System Team");
        return new ResponseEntity<>("Password reset successfully.", HttpStatus.OK);
    }

    /**
     * Retrieves employees based on their status (e.g., "ACTIVE", "INACTIVE", "UNDER_PIP").
     *
     * @param status The status to filter by.
     * @return ResponseEntity with a list of Employee objects.
     */
    @Override
    public ResponseEntity<?> getEmployeesByStatus(String status) {
        return new ResponseEntity<>(employeeRepository.findByStatus(status), HttpStatus.OK);
    }

    /**
     * Retrieves employees based on their role.
     *
     * @param role The role to filter by (e.g., "ADMIN", "HR", "MANAGER", "EMPLOYEE").
     * @return ResponseEntity with a list of Employee objects or a BAD_REQUEST if the role is invalid.
     */
    @Override
    public ResponseEntity<?> getEmployeesByRole(String role) {
        try {
            Role r = Role.valueOf(role.toUpperCase()); // Ensure role is uppercase for Enum.valueOf
            return new ResponseEntity<>(employeeRepository.findByRole(r), HttpStatus.OK); // Changed from BAD_REQUEST to OK
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid role: " + role, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Assigns a manager to an employee.
     * Ensures both employee and manager exist, and the manager has a MANAGER or ADMIN role.
     *
     * @param empId The UUID of the employee.
     * @param managerId The UUID of the manager to assign.
     * @return ResponseEntity with the updated Employee object or an error message.
     */
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
        Employee updatedEmployee = employeeRepository.save(emp); // Save the employee first

        // Notification to the employee who got a new manager (ADDED)
        String empNotificationTitle = "New Manager Assigned";
        String empNotificationMessage = "You have been assigned a new manager: " + updatedEmployee.getManager().getName() + ".";
        notificationService.createNotification(updatedEmployee, empNotificationTitle, empNotificationMessage, "INFO");

        // Notification to the newly assigned manager (ADDED)
        String mgrNotificationTitle = "New Team Member Assigned";
        String mgrNotificationMessage = "You have been assigned a new team member: " + updatedEmployee.getName() + ".";
        notificationService.createNotification(updatedEmployee.getManager(), mgrNotificationTitle, mgrNotificationMessage, "INFO");


        return new ResponseEntity<>(updatedEmployee, HttpStatus.OK);
    }

    /**
     * Retrieves a list of team members for a given manager.
     *
     * @param managerId The UUID of the manager.
     * @return ResponseEntity with a list of Employee objects who report to this manager, or NOT_FOUND if manager not found.
     */
    @Override
    public ResponseEntity<?> getTeamMembers(UUID managerId) {
        Optional<Employee> mgrOpt = employeeRepository.findById(managerId);
        if (mgrOpt.isEmpty()) {
            return new ResponseEntity<>("Manager not found with ID: " + managerId, HttpStatus.NOT_FOUND);
        }

        List<Employee> teamMembers = employeeRepository.findByManager_EmployeeId(managerId);
        return new ResponseEntity<>(teamMembers, HttpStatus.OK);
    }

    /**
     * Updates the status of an employee.
     *
     * @param id The UUID of the employee.
     * @param status The new status to set.
     * @return ResponseEntity with the updated Employee object or NOT_FOUND.
     */
    @Override
    public ResponseEntity<?> updateStatus(UUID id, String status) {
        Optional<Employee> empOpt = employeeRepository.findById(id);
        if (empOpt.isEmpty()) {
            return new ResponseEntity<>("Employee not found with ID: " + id, HttpStatus.NOT_FOUND);
        }

        Employee emp = empOpt.get();
        emp.setStatus(status);
        Employee updatedEmp = employeeRepository.save(emp); // Save the employee first

        // Notification to the employee whose status was updated (ADDED)
        String empNotificationTitle = "Your Status Updated";
        String empNotificationMessage = "Your status has been updated to: " + updatedEmp.getStatus() + ".";
        notificationService.createNotification(updatedEmp, empNotificationTitle, empNotificationMessage, "INFO");

        // Notification to all ADMINs about the status change (ADDED)
        List<Employee> admins = employeeRepository.findByRole(Role.ADMIN);
        for (Employee admin : admins) {
            String adminNotificationTitle = "Employee Status Changed";
            String adminNotificationMessage = updatedEmp.getName() + "'s status has been changed to " + updatedEmp.getStatus() + ".";
            notificationService.createNotification(admin, adminNotificationTitle, adminNotificationMessage, "INFO");
        }
        return new ResponseEntity<>(updatedEmp, HttpStatus.OK);
    }
    /**
     * Marks an employee as being "UNDER_PIP" (Performance Improvement Plan).
     * Prevents adding an employee already under PIP.
     *
     * @param employeeId The UUID of the employee.
     * @return ResponseEntity with a success message or a BAD_REQUEST if already under PIP.
     */
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

        String notificationTitle = "Performance Improvement Plan";
        String notificationMessage = "You have been placed on a Performance Improvement Plan. Please check your details.";
        notificationService.createNotification(emp, notificationTitle, notificationMessage, "ALERT");

        if (emp.getManager() != null) {
            String managerNotificationMessage = emp.getName() + " has been placed on a PIP. You have been notified.";
            notificationService.createNotification(emp.getManager(), "PIP Alert", managerNotificationMessage, "ALERT");
        }

        return new ResponseEntity<>("Employee added to PIP successfully", HttpStatus.OK);
    }

    /**
     * Allows a logged-in employee to reset their password using their old password.
     * Clears temporary password flags upon successful reset.
     *
     * @param employeeId The UUID of the employee resetting the password.
     * @param oldPassword The employee's current password.
     * @param newPassword The new password to set.
     * @return ResponseEntity with a success message or an error message (incorrect old password, employee not found).
     */
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

        // Notification: Password reset successful (ADDED)
        String notificationTitle = "Password Reset Successful";
        String notificationMessage = "Your password has been successfully reset.";
        notificationService.createNotification(employee, notificationTitle, notificationMessage, "INFO");

        String to = employee.getEmail();
        String subject = "Password Reset Successful";
        String message = "Hi " + employee.getName() + ",\n\nYour password has been successfully reset.\n\n" +
                "If you did not request this change, please contact the admin immediately.\n\n" +
                "Regards,\nPIP Review System Team";

        mailService.sendMail(to, subject, message);

        return new ResponseEntity<>("Password reset successfully", HttpStatus.OK);
    }

    /**
     * Retrieves the PIP status of an employee.
     *
     * @param employeeId The UUID of the employee.
     * @return ResponseEntity with a map containing the PIP status or NOT_FOUND.
     */
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

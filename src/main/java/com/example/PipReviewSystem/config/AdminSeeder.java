package com.example.PipReviewSystem.config;

import com.example.PipReviewSystem.entity.Employee;
import com.example.PipReviewSystem.enums.Role;
import com.example.PipReviewSystem.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
public class AdminSeeder {

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.name}")
    private String adminName;

    @Value("${admin.department}")
    private String adminDepartment;

    @Value("${admin.designation}")
    private String adminDesignation;

    @Bean
    public CommandLineRunner seedAdmin(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            Optional<Employee> existingAdmin = employeeRepository.findByEmail(adminEmail);
            if (existingAdmin.isEmpty()) {
                Employee admin = new Employee();
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setName(adminName);
                admin.setDepartment(adminDepartment);
                admin.setDesignation(adminDesignation);
                admin.setRole(Role.ADMIN);
                admin.setStatus("ACTIVE");

                employeeRepository.save(admin);
                System.out.println("✅ Admin created via properties.");
            } else {
                System.out.println("ℹ️ Admin already exists.");
            }
        };
    }
}

package com.example.PipReviewSystem.config;

import com.example.PipReviewSystem.config.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Swagger and public routes
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers( "/api/employees/login").permitAll()
                        .requestMatchers("api/employees/forgot-password", "/api/employees/logout").permitAll()

                        // Employee
                                .requestMatchers( "/api/employees/reset-password").permitAll()
                        .requestMatchers("/api/employees/all").hasAuthority("ADMIN")
                        .requestMatchers("/api/employees/signup").hasAnyAuthority("ADMIN","HR")
                        .requestMatchers("/api/employees/{id}").hasAnyAuthority("ADMIN", "HR")
                        .requestMatchers("/api/employees/update/{id}").hasAnyAuthority("ADMIN", "HR")
                        .requestMatchers("/api/employees/delete/{id}").hasAnyAuthority("ADMIN", "HR")
                        .requestMatchers("/api/employees/assign-manager").hasAnyAuthority("ADMIN", "HR", "MANAGER")
                        .requestMatchers("/api/employees/role/{role}").hasAnyAuthority("ADMIN", "HR")
                        .requestMatchers("/api/employees/add-to-pip/{employeeId}").hasAnyAuthority("MANAGER","ADMIN")
//                        .requestMatchers("/api/employees/delete/{id}").hasAnyAuthority("ADMIN", "HR")
//                        .requestMatchers("/api/employees/delete/{id}").hasAnyAuthority("ADMIN", "HR")




                        // Audit
                        .requestMatchers("/api/audit/add").hasAnyAuthority("ADMIN", "HR")
                        .requestMatchers("/api/audit/all").hasAuthority("ADMIN")
                        .requestMatchers("/api/audit/user/**").hasAnyAuthority("ADMIN", "HR")
                        .requestMatchers("/api/audit/delete/**").hasAuthority("ADMIN")

                        // PIP
                        .requestMatchers("/api/pip/start").hasAnyAuthority("MANAGER", "HR", "ADMIN")
                        .requestMatchers("/api/pip/{pipId}/update").hasAnyAuthority("MANAGER", "HR")
                        .requestMatchers("/api/pip/{pipId}/complete").hasAnyAuthority("MANAGER", "HR")
                        .requestMatchers("/api/pip/employee/{id}").hasAnyAuthority("MANAGER", "HR", "EMPLOYEE", "ADMIN")
                        .requestMatchers("/api/pip/all").hasAuthority("ADMIN")

                        // Feedback
                        .requestMatchers("/api/feedbacks/add").hasAnyAuthority("EMPLOYEE", "MANAGER", "HR")
                        .requestMatchers("/api/feedbacks").hasAnyAuthority("ADMIN", "HR")
                        .requestMatchers("/api/feedbacks/{id}").hasAnyAuthority("ADMIN", "HR", "MANAGER")
                        .requestMatchers("/api/feedbacks/toUser/**").hasAnyAuthority("MANAGER", "HR", "EMPLOYEE")
                        .requestMatchers("/api/feedbacks/fromUser/**").hasAnyAuthority("MANAGER", "HR", "EMPLOYEE")
                        .requestMatchers("/api/feedbacks/type/**").hasAnyAuthority("ADMIN", "HR")
                        .requestMatchers("/api/feedbacks/**").authenticated()

                        // Reports
                        .requestMatchers("/api/reports").hasAnyAuthority("ADMIN", "HR")
                        .requestMatchers("/api/reports/{id}").hasAnyAuthority("ADMIN", "HR")
                        .requestMatchers("/api/reports/employee/**").hasAnyAuthority("MANAGER", "HR", "EMPLOYEE")
                        .requestMatchers("/api/reports/type/**").hasAnyAuthority("ADMIN", "HR")
                        .requestMatchers("/api/reports").hasAnyAuthority("MANAGER", "HR")
                        .requestMatchers("/api/reports/{id}").hasAnyAuthority("ADMIN", "HR")

                        // Skill Gap
                        .requestMatchers("/api/skill-gap").hasAnyAuthority("HR", "MANAGER", "ADMIN")
                        .requestMatchers("/api/skill-gap/{id}").hasAnyAuthority("HR", "MANAGER", "ADMIN")
                        .requestMatchers("/api/skill-gap/employee/**").hasAnyAuthority("HR", "MANAGER", "EMPLOYEE")

                        // Performance Reviews
                        .requestMatchers("/api/performance-reviews").hasAnyAuthority("HR", "MANAGER", "ADMIN")
                        .requestMatchers("/api/performance-reviews/{id}").hasAnyAuthority("HR", "MANAGER", "ADMIN")
                        .requestMatchers("/api/performance-reviews/employee/**").hasAnyAuthority("HR", "MANAGER", "EMPLOYEE", "ADMIN")
                        .requestMatchers("/api/performance-reviews/reviewer/**").hasAnyAuthority("HR", "MANAGER")
                        .requestMatchers("/api/performance-reviews/type/**").hasAnyAuthority("HR", "MANAGER")
                        .requestMatchers("/api/performance-reviews/period/**").hasAnyAuthority("HR", "MANAGER")
                        .requestMatchers("/api/performance-reviews/rating-range").hasAnyAuthority("HR", "MANAGER", "ADMIN")

                        // Catch all for other endpoints
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}












//package com.example.PipReviewSystem.config;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    @Autowired
//    private JwtAuthFilter jwtAuthFilter;
//
//    @Autowired
//    private UserDetailsService userDetailsService;
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        return http.csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/employees/signup", "/api/employees/login").permitAll()
//                        .requestMatchers("/api/employees/all").hasAuthority("ADMIN")
//                        .requestMatchers("/api/employees/{id}").hasAnyAuthority("ADMIN", "HR")
//                        .requestMatchers("/api/employees/update/{id}").hasAnyAuthority("ADMIN", "HR")
//                        .requestMatchers("/api/employees/delete/{id}").hasAnyAuthority("ADMIN", "HR")
//                        //Audit
//                        .requestMatchers("/api/audit/add").hasAnyAuthority("ADMIN", "HR")
//                        .requestMatchers("/api/audit/all").hasAuthority("ADMIN")
//                        .requestMatchers("/api/audit/user/**").hasAnyAuthority("ADMIN", "HR")
//                        .requestMatchers("/api/audit/delete/**").hasAuthority("ADMIN")
//                        //pip
//                        .requestMatchers("/api/pip/start").hasAnyAuthority("MANAGER", "HR", "ADMIN")
//                        .requestMatchers("/api/pip/{pipId}/update").hasAnyAuthority("MANAGER", "HR")
//                        .requestMatchers("/api/pip/{pipId}/complete").hasAnyAuthority("MANAGER", "HR")
//                        .requestMatchers("/api/pip/employee/{id}").hasAnyAuthority("MANAGER", "HR", "EMPLOYEE", "ADMIN")
//                        .requestMatchers("/api/pip/all").hasAuthority("ADMIN")
//
//                        //feedback
//
//                        .requestMatchers("/api/feedbacks/add").hasAnyAuthority("EMPLOYEE", "MANAGER", "HR")
//                        .requestMatchers("/api/feedbacks").hasAnyAuthority("ADMIN","HR") // all feedbacks
//                        .requestMatchers("/api/feedbacks/{id}").hasAnyAuthority("ADMIN", "HR", "MANAGER")
//                        .requestMatchers("/api/feedbacks/{id}").hasAnyAuthority("ADMIN", "HR") // update, delete
//
//                        .requestMatchers("/api/feedbacks/toUser/**").hasAnyAuthority("MANAGER", "HR", "EMPLOYEE")
//                        .requestMatchers("/api/feedbacks/fromUser/**").hasAnyAuthority("MANAGER", "HR", "EMPLOYEE")
//                        .requestMatchers("/api/feedbacks/type/**").hasAnyAuthority("ADMIN", "HR")
//
//                        .requestMatchers("/api/feedbacks/**").authenticated()
//
//
//                        // Report
//
//                        // Reports
//                        .requestMatchers("/api/reports").hasAnyAuthority( "ADMIN","HR") // GET all reports
//                        .requestMatchers("/api/reports/{id}").hasAnyAuthority("ADMIN", "HR") // GET by ID
//                        .requestMatchers("/api/reports/employee/**").hasAnyAuthority("MANAGER", "HR", "EMPLOYEE") // GET by employee
//                        .requestMatchers("/api/reports/type/**").hasAnyAuthority("ADMIN", "HR") // GET by type
//                        .requestMatchers("/api/reports").hasAnyAuthority("MANAGER", "HR") // POST - create
//                        .requestMatchers("/api/reports/{id}").hasAnyAuthority("ADMIN", "HR") // PUT, DELETE
//
//                         //skillgap
//                        .requestMatchers("/api/skill-gap").hasAnyAuthority("HR", "MANAGER", "ADMIN") // POST
//                        .requestMatchers("/api/skill-gap/{id}").hasAnyAuthority("HR", "MANAGER", "ADMIN") // GET, PUT, DELETE
//                        .requestMatchers("/api/skill-gap").hasAnyAuthority("HR", "MANAGER", "ADMIN") // GET all
//                        .requestMatchers("/api/skill-gap/employee/**").hasAnyAuthority("HR", "MANAGER", "EMPLOYEE") // GET by employee
//
//                        //performancereview
//
//
//                        .requestMatchers("/api/performance-reviews").hasAnyAuthority("HR", "MANAGER")
//                        .requestMatchers("/api/performance-reviews/{id}").hasAnyAuthority("HR", "MANAGER", "ADMIN")
//                        .requestMatchers("/api/performance-reviews").hasAnyAuthority("HR", "MANAGER", "ADMIN")
//                        .requestMatchers("/api/performance-reviews/employee/**").hasAnyAuthority("HR", "MANAGER", "EMPLOYEE", "ADMIN")
//
//                        // GET by reviewer
//                        .requestMatchers("/api/performance-reviews/reviewer/**").hasAnyAuthority("HR", "MANAGER")
//
//                        // GET by type
//                        .requestMatchers("/api/performance-reviews/type/**").hasAnyAuthority("HR", "MANAGER")
//
//                        // GET by period
//                        .requestMatchers("/api/performance-reviews/period/**").hasAnyAuthority("HR", "MANAGER")
//
//                        // GET by rating range
//                        .requestMatchers("/api/performance-reviews/rating-range").hasAnyAuthority("HR", "MANAGER", "ADMIN")
//
//                        // Everything else requires authentication
//                        .anyRequest().authenticated()
//
//
//
//                        .requestMatchers(
//                                "/swagger-ui/**",
//                                "/v3/api-docs/**",
//                                "/swagger-ui.html"
//
//
//                        ).permitAll()
//
//                        .anyRequest().authenticated()
//                )
//                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authenticationProvider(authenticationProvider())
//                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
//                .build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setUserDetailsService(userDetailsService);
//        provider.setPasswordEncoder(passwordEncoder());
//        return provider;
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
//}

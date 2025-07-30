package com.example.PipReviewSystem.config;

import com.example.PipReviewSystem.config.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

                        //employee
                                .requestMatchers("/api/employees/reset-password/request-otp").permitAll()
                                .requestMatchers("/api/employees/reset-password/verify-otp-reset").permitAll()
                                .requestMatchers( "/api/employees/reset-password/request-link").permitAll()
                                .requestMatchers("/api/employees/reset-password/reset-with-token").permitAll()
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
                                .requestMatchers("/api/feedbacks/add").hasAnyAuthority("EMPLOYEE", "MANAGER")
                                .requestMatchers("/api/feedbacks").hasAnyAuthority("ADMIN", "HR", "MANAGER")
                                .requestMatchers(HttpMethod.GET, "/api/feedbacks/{id}").hasAnyAuthority("ADMIN", "HR", "MANAGER")
                                .requestMatchers(HttpMethod.PUT, "/api/feedbacks/{id}").hasAnyAuthority("MANAGER", "HR")
                                .requestMatchers(HttpMethod.DELETE, "/api/feedbacks/{id}").hasAuthority("HR")
                                .requestMatchers("/api/feedbacks/toUser/**").hasAnyAuthority("EMPLOYEE", "MANAGER", "HR")
                                .requestMatchers("/api/feedbacks/fromUser/**").hasAnyAuthority("EMPLOYEE", "MANAGER", "HR")
                                .requestMatchers("/api/feedbacks/type/**").hasAnyAuthority("HR", "MANAGER")



                                // Reports
                               .requestMatchers("/api/reports").hasAnyAuthority("ADMIN", "HR","MANAGER")
                                .requestMatchers("/api/reports/{id}").hasAnyAuthority("ADMIN", "HR")
                                .requestMatchers("/api/reports/employee/**").hasAnyAuthority("MANAGER", "HR", "EMPLOYEE","ADMIN")
                                .requestMatchers("/api/reports/type/**").hasAnyAuthority("ADMIN", "HR")
                              //  .requestMatchers("/api/reports").permitAll()
                                .requestMatchers("/api/reports/**").hasAuthority("MANAGER")
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











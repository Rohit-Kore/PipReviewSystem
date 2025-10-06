package com.example.PipReviewSystem.config;

import com.example.PipReviewSystem.entity.Employee;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Employee employee;

    public CustomUserDetails(Employee employee) {

        this.employee = employee;

    }

    @Override

    public String getUsername() {

        return employee.getEmail();  // <-- THIS IS YOUR CUSTOM IMPLEMENTATION

    }

    @Override

    public String getPassword() {

        return employee.getPassword();

    }

    @Override

    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(new SimpleGrantedAuthority(employee.getRole().name()));

    }

    // Implement other methods like isAccountNonExpired, etc. as needed

    @Override

    public boolean isAccountNonExpired() { return true; }

    @Override

    public boolean isAccountNonLocked() { return true; }

    @Override

    public boolean isCredentialsNonExpired() { return true; }

    @Override

    public boolean isEnabled() { return true; }

}

 
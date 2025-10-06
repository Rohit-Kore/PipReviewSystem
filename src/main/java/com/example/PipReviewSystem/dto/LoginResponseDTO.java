package com.example.PipReviewSystem.dto;

import com.example.PipReviewSystem.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private UUID id;
    private String name;
    private String email;
    private Role role;
    private String department;
    private String designation;
    private boolean passwordChangeRequired;
    private String token;
}
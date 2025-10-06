package com.example.PipReviewSystem.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyOtpResetRequest {

        public String email;

        public String otp;

        public String newPassword;

    }




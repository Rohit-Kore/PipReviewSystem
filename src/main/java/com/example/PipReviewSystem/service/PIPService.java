package com.example.PipReviewSystem.service;

import com.example.PipReviewSystem.entity.PIP;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface PIPService {
    ResponseEntity<?> startPip(PIP pip);
    ResponseEntity<?> updatePip(Long pipId, PIP pip);
    ResponseEntity<?> completePip(Long pipId, String outcome);
    ResponseEntity<?> getPipsByEmployee(UUID employeeId);
    ResponseEntity<?> getAllPips();
}

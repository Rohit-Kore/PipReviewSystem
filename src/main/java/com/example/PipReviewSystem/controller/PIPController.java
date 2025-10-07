package com.example.PipReviewSystem.controller;

import com.example.PipReviewSystem.entity.PIP;
import com.example.PipReviewSystem.service.PIPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/pip")
@CrossOrigin("*")
public class PIPController {

    @Autowired
    private PIPService pipService;

    @PreAuthorize("hasAnyAuthority('MANAGER', 'HR','ADMIN')")

    @PostMapping("/start")
    public ResponseEntity<?> startPip(@RequestBody PIP pip) {
        return pipService.startPip(pip);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'HR')")
    @PutMapping("/{pipId}/update")
    public ResponseEntity<?> updatePip(@PathVariable Long pipId, @RequestBody PIP pip) {
        return pipService.updatePip(pipId, pip);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'HR')")
    @PostMapping("/{pipId}/complete")
    public ResponseEntity<?> completePip(@PathVariable Long pipId, @RequestParam String outcome) {
        return pipService.completePip(pipId, outcome);
    }


    @PreAuthorize("hasAnyAuthority('MANAGER', 'HR', 'EMPLOYEE', 'ADMIN')")
    @GetMapping("/employee/{id}")
    public ResponseEntity<?> getPipsByEmployee(@PathVariable UUID id) {
        return pipService.getPipsByEmployee(id);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<?> getAllPips() {
        return pipService.getAllPips();
    }
}
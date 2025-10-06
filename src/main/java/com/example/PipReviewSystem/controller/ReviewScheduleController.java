package com.example.PipReviewSystem.controller;

import com.example.PipReviewSystem.dto.ScheduleReviewRequest;
import com.example.PipReviewSystem.entity.ReviewSchedule;
import com.example.PipReviewSystem.service.ReviewScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewScheduleController {

    private final ReviewScheduleService scheduleService;
    @PreAuthorize("hasAnyAuthority('MANAGER', 'EMPLOYEE')")

    @PostMapping("/schedule")
    public ResponseEntity<ReviewSchedule> schedule(@RequestBody ScheduleReviewRequest req) {
        ReviewSchedule saved = scheduleService.scheduleReview(
                UUID.fromString(req.getEmployeeId()),
                LocalDateTime.parse(req.getReviewDate()),
                req.getScheduledBy(),
                req.isNotifyNow()
        );
        return ResponseEntity.ok(saved);
    }
}


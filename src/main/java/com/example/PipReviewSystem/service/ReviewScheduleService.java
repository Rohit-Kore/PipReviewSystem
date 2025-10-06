package com.example.PipReviewSystem.service;


import com.example.PipReviewSystem.entity.Employee;
import com.example.PipReviewSystem.entity.ReviewSchedule;
import com.example.PipReviewSystem.repository.EmployeeRepository;
import com.example.PipReviewSystem.repository.ReviewScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewScheduleService {

    private final ReviewScheduleRepository scheduleRepo;
    private final EmployeeRepository employeeRepository;
    private final NotificationService notificationService;

    public ReviewSchedule scheduleReview(UUID employeeId, LocalDateTime reviewDate, String scheduledByEmail, boolean notifyNow) {
        Employee emp = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        ReviewSchedule rs = new ReviewSchedule();
        rs.setEmployee(emp);
        rs.setReviewDate(reviewDate);
        rs.setScheduledBy(scheduledByEmail);
        ReviewSchedule saved = scheduleRepo.save(rs);

        if (notifyNow) {
            notificationService.createNotification(
                    emp,
                    "Performance Review Scheduled",
                    "Your performance review is scheduled on " + reviewDate + ".",
                    "INFO"
            );
        }

        return saved;
    }
}

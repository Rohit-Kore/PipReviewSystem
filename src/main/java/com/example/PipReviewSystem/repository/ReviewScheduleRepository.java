package com.example.PipReviewSystem.repository;

import com.example.PipReviewSystem.entity.ReviewSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ReviewScheduleRepository extends JpaRepository<ReviewSchedule, Long> {


    List<ReviewSchedule> findByReviewDateBetweenAndReminder3DaysSentFalse(LocalDateTime start, LocalDateTime end);


    List<ReviewSchedule> findByReviewDateBetweenAndReminder1DaySentFalse(LocalDateTime start, LocalDateTime end);

    // Optional: finds schedules for a specific employee
    List<ReviewSchedule> findByEmployeeEmployeeId(UUID employeeId);
}
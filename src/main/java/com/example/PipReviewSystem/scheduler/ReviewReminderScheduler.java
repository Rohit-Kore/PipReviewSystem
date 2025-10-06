package com.example.PipReviewSystem.scheduler;

import com.example.PipReviewSystem.entity.ReviewSchedule;
import com.example.PipReviewSystem.repository.ReviewScheduleRepository;
import com.example.PipReviewSystem.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReviewReminderScheduler {

    private final ReviewScheduleRepository scheduleRepo;
    private final NotificationService notificationService;

    // Run every hour at minute 0
    @Scheduled(cron = "0 0 * * * *")
    public void sendReminders() {
        log.info("Running review reminder scheduler at {}", LocalDateTime.now());
        LocalDateTime now = LocalDateTime.now();

        // 3-day window: Reminders for reviews scheduled in the next 3 days.
        LocalDateTime threeDayWindowStart = now;
        LocalDateTime threeDayWindowEnd = now.plusDays(3);

        List<ReviewSchedule> threeDayList =
                scheduleRepo.findByReviewDateBetweenAndReminder3DaysSentFalse(threeDayWindowStart, threeDayWindowEnd);

        for (ReviewSchedule rs : threeDayList) {
            LocalDateTime reviewDate = rs.getReviewDate();
            // This extra check ensures the reminder is sent exactly 3 days before the review.
            if (now.isAfter(reviewDate.minusDays(3)) && now.isBefore(reviewDate.minusDays(2))) {
                notificationService.createNotification(
                        rs.getEmployee(),
                        "Performance Review Reminder",
                        "Reminder: Your performance review is scheduled on " + reviewDate.toLocalDate() + " (in 3 days).",
                        "REMINDER"
                );
                rs.setReminder3DaysSent(true);
                scheduleRepo.save(rs);
                log.info("Sent 3-day reminder for employee: {}", rs.getEmployee().getEmail());
            }
        }

        // 1-day window: Reminders for reviews scheduled in the next 1 day.
        LocalDateTime oneDayWindowStart = now;
        LocalDateTime oneDayWindowEnd = now.plusDays(1);

        List<ReviewSchedule> oneDayList =
                scheduleRepo.findByReviewDateBetweenAndReminder1DaySentFalse(oneDayWindowStart, oneDayWindowEnd);

        for (ReviewSchedule rs : oneDayList) {
            LocalDateTime reviewDate = rs.getReviewDate();
            // This extra check ensures the reminder is sent exactly 1 day before the review.
            if (now.isAfter(reviewDate.minusDays(1)) && now.isBefore(reviewDate)) {
                notificationService.createNotification(
                        rs.getEmployee(),
                        "Performance Review Reminder",
                        "Reminder: Your performance review is scheduled tomorrow (" + reviewDate.toLocalDate() + ").",
                        "REMINDER"
                );
                rs.setReminder1DaySent(true);
                scheduleRepo.save(rs);
                log.info("Sent 1-day reminder for employee: {}", rs.getEmployee().getEmail());
            }
        }
    }
}
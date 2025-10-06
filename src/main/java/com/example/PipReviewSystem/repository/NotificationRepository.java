package com.example.PipReviewSystem.repository;

import com.example.PipReviewSystem.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByEmployeeEmployeeId(UUID userId);
    List<Notification> findByIsReadFalseAndEmployeeEmployeeId(UUID userId);

    List<Notification> findByEmployeeEmail(String email);
}

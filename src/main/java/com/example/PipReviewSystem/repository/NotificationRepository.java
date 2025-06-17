package com.example.PipReviewSystem.repository;

import com.example.PipReviewSystem.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByEmployeeEmployeeId(Long userId);
    List<Notification> findByIsReadFalseAndEmployeeEmployeeId(Long userId);
}

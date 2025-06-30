package com.example.PipReviewSystem.repository;

import com.example.PipReviewSystem.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByToUserEmployeeId(UUID toUserId);

    List<Feedback> findByFromUserEmployeeId(UUID fromUserId);

    List<Feedback> findByFeedbackType(String feedbackType);
}

package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Feedback;
import com.project.KoiBookingSystem.enums.FeedbackType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    Feedback findTopByOrderByIdDesc();

    List<Feedback> findByTypeAndStatusTrue(FeedbackType type);

    Feedback findByFeedbackId(String feedbackId);
}

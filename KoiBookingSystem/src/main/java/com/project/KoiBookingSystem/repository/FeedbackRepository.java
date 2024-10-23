package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    Feedback findTopByOrderByIdDesc();

    List<Feedback> findByStatusTrue();

    Feedback findByFeedbackId(String feedbackId);

    List<Feedback> findByTour_TourId(String tourId);
}

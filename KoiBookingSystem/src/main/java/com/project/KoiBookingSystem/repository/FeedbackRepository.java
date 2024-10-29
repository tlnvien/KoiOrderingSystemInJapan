package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    Feedback findTopByOrderByIdDesc();

    List<Feedback> findByStatusTrue();

    Feedback findByFeedbackId(String feedbackId);

    List<Feedback> findByTour_TourIdAndStatusTrue(String tourId);

    List<Feedback> findByCustomer_UserIdAndStatusTrue(String userId);

    List<Feedback> findByRatingLessThanEqualAndStatusTrue(int rating);

    List<Feedback> findByRatingGreaterThanAndStatusTrue(int rating);

    List<Feedback> findByTour_TourId(String tourId);

    List<Feedback> findByRatingLessThanEqual(int rating);

    List<Feedback> findByRatingGreaterThan(int rating);

    List<Feedback> findByCustomer_UserId(String userId);
}

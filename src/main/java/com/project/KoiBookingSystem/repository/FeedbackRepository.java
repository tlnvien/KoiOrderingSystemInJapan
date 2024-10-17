package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.entity.Feedback;
import com.project.KoiBookingSystem.model.response.FeedbackResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    @Query("SELECT new com.project.KoiBookingSystem.model.response.FeedbackResponse(f.id, f.content, f.rating, a.username) " +
            "FROM Feedback f JOIN f.staff a")
    List<FeedbackResponse> findFeedbacksByStaff();
}
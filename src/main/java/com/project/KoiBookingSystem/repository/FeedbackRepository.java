package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.entity.Feedback;
import com.project.KoiBookingSystem.entity.Tour;
import com.project.KoiBookingSystem.model.response.FeedbackResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    @Query("SELECT new com.project.KoiBookingSystem.model.response.FeedbackResponse(f.id, f.feedbackComment, f.feedbackDate, f.rating, s.username, t.tourID) " +
            "FROM Feedback f " +
            "JOIN f.staff s " +
            "JOIN f.tourFeedback t") // Lấy thông tin tourID từ bảng Tour
    List<FeedbackResponse> findFeedbacksByStaff();
}

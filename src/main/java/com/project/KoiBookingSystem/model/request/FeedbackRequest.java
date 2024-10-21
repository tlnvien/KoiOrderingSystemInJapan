package com.project.KoiBookingSystem.model.request;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.model.response.BookingAvailableTour;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.util.Date;

@Data
public class FeedbackRequest {

    private String feedbackComment;

    private int rating;

    private String staffID;

    private String tourID;

}

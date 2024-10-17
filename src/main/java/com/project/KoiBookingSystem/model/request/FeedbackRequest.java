package com.project.KoiBookingSystem.model.request;

import com.project.KoiBookingSystem.entity.Account;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
public class FeedbackRequest {

    private String content;

    private int rating;

    private String userId;

}

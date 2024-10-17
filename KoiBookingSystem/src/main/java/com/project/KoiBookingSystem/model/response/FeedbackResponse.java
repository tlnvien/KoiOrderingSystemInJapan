package com.project.KoiBookingSystem.model.response;

import com.project.KoiBookingSystem.enums.FeedbackType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class FeedbackResponse {

    private String feedbackId;

    private String customerId;

    private String staffId;

    private String farmName;

    private String koiSpecies;

    private String comment;

    private int rating;

    @Enumerated(EnumType.STRING)
    private FeedbackType type;
}

package com.project.KoiBookingSystem.model.response;

import lombok.Data;

@Data
public class FeedbackResponse {

    private String feedbackId;

    private String customerId;

    private String tourId;

    private String comment;

    private int rating;

}

package com.project.KoiBookingSystem.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FeedbackResponse {

    private String feedbackId;

    private String customerId;

    private String tourId;

    private String comment;

    private int rating;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime feedbackDate;

}

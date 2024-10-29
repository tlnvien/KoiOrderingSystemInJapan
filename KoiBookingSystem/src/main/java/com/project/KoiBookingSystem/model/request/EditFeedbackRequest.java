package com.project.KoiBookingSystem.model.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class EditFeedbackRequest {

    private String comment;

    @Min(value = 1, message = "Rating không hợp lệ!")
    @Max(value = 5, message = "Rating không hợp lệ!")
    private int rating;
}

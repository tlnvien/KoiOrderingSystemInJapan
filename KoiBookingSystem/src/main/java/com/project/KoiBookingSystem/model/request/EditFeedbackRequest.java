package com.project.KoiBookingSystem.model.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class EditFeedbackRequest {

    private String comment;

    @Min(value = 1, message = "Invalid rating!")
    @Max(value = 5, message = "Invalid rating!")
    private int rating;
}

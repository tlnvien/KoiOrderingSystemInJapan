package com.project.KoiBookingSystem.model.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class FeedbackRequest {

    private String comment;

    @Min(value = 0, message = "Rating không hợp lệ!")
    @Max(value = 5 ,message = "Rating không hợp lệ!")
    private int rating;

}

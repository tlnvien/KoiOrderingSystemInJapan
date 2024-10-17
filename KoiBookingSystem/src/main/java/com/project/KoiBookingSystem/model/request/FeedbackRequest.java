package com.project.KoiBookingSystem.model.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class FeedbackRequest {

    private String comment;

    @Min(value = 0, message = "Invalid rating!")
    @Max(value = 5 ,message = "Invalid rating!")
    private int rating;

    private String farmName;

    private String koiSpecies;

    private String staffId;

}

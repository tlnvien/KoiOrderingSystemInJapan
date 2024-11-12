package com.project.KoiBookingSystem.model.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BookingAvailableTour {
    @NotBlank(message = "Can not be blank!!!")
    private String tourID;
}

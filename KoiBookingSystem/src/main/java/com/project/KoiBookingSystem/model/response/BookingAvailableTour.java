package com.project.KoiBookingSystem.model.response;

import com.project.KoiBookingSystem.enums.TourType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BookingAvailableTour {
    @NotBlank(message = "Can not be blank!!!")
    private String tourID;

//    @NotBlank(message = "Tour Name can not be blank!!!")
//    private String tourName;
//
//    @Enumerated(EnumType.STRING)
//    private TourType tourType;

}


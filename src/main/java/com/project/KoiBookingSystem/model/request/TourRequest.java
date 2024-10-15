package com.project.KoiBookingSystem.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.KoiBookingSystem.enums.TourType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TourRequest {

    @NotBlank(message = "Tour ID can not be empty!")
    @Column(unique = true)
    private String tourID;

    @NotBlank(message = "Tour Name can not be empty!")
    private String tourName;

    @Column(nullable = false)
    @Min(value = 0, message = "Invalid Tour Max Participant")
    private int maxParticipants;

    @Column(nullable = false)
    @Min(value = 0, message = "Invalid Remain Seat!")
    private int remainSeat;


    @Column(nullable = false)
    @Future(message = "Invalid Tour End Date!")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Column(nullable = false)
    @Future(message = "Invalid Tour End Date!")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @NotBlank(message = "Tour Description can not be empty!")
    private String description;

    private String consulting;

    @Enumerated(EnumType.STRING)
    private TourType type;

    @Min(value = 0, message = "Invalid Price")
    private float price;



}

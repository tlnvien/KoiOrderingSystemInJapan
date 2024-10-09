package com.project.KoiBookingSystem.model.response;

<<<<<<< HEAD
import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.enums.TourType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TourResponse {

    private String tourId;
=======
import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.enums.TourType;

import java.time.LocalDateTime;

public class TourResponse {

    private String tourID;
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec

    private String tourName;

    private int maxParticipants;

    private int remainSeat;

<<<<<<< HEAD
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate departureDate;

    private String duration;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private String description;

    private String consulting;

    @Enumerated(EnumType.STRING)
    private TourType tourType;

    private Double price;

    private String salesId;

    private List<TourScheduleResponse> tourSchedules;
=======
    private LocalDateTime departureDate;

    private LocalDateTime endDate;

    private String description;

    private Account consulting;

    private TourType tourType;

    private double price;

    private Account manager;

>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
}

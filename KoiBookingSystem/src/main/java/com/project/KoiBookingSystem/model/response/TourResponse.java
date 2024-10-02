package com.project.KoiBookingSystem.model.response;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.enums.TourType;

import java.time.LocalDateTime;

public class TourResponse {

    private String tourID;

    private String tourName;

    private int maxParticipants;

    private int remainSeat;

    private LocalDateTime departureDate;

    private LocalDateTime endDate;

    private String description;

    private Account consulting;

    private TourType tourType;

    private double price;

    private Account manager;

}

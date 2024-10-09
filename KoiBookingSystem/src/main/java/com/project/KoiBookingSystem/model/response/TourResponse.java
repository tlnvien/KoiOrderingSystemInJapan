package com.project.KoiBookingSystem.model.response;

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

    private String tourName;

    private int maxParticipants;

    private int remainSeat;

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
}

package com.project.KoiBookingSystem.model.request;

import com.project.KoiBookingSystem.entity.Farm;
import com.project.KoiBookingSystem.entity.Tour;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TourScheduleRequest {

    private String farm;

    @NotNull(message = "Start Date can not be empty!")
    private LocalDateTime startDate;

    @NotNull(message = "End Date can not be empty!")
    private LocalDateTime endDate;
}

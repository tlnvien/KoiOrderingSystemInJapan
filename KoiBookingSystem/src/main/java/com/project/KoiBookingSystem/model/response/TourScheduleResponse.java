package com.project.KoiBookingSystem.model.response;

import com.project.KoiBookingSystem.entity.Farm;
import com.project.KoiBookingSystem.entity.Tour;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TourScheduleResponse {
    private Tour tour;

    private Farm farm;

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}

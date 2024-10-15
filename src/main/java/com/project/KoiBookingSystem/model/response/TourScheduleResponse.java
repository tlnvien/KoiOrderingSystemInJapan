package com.project.KoiBookingSystem.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.KoiBookingSystem.entity.Farm;
import com.project.KoiBookingSystem.entity.Tour;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TourScheduleResponse {
    private TourResponse tour;

    private FarmResponse farm;
}

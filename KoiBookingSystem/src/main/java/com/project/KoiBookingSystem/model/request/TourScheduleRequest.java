package com.project.KoiBookingSystem.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.KoiBookingSystem.entity.Farm;
import com.project.KoiBookingSystem.entity.Tour;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TourScheduleRequest {

    @NotBlank(message = "Tour Farm can not be blank!")
    private String farmId;

    @NotNull(message = "Start Date can not be null")
    private LocalDateTime startDate;

    @NotNull(message = "Start Date can not be null")
    private LocalDateTime endDate;

}

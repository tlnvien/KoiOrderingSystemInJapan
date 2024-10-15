package com.project.KoiBookingSystem.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.KoiBookingSystem.entity.Farm;
import com.project.KoiBookingSystem.entity.Tour;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TourScheduleRequest {

    @Column(nullable = false)
    private String farm;

}

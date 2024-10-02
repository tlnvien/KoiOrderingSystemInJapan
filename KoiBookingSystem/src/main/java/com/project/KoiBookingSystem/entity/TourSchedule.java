package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class TourSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "tour_id", nullable = false, referencedColumnName = "tourID")
    private Tour tour;

    @ManyToOne
    @JoinColumn(name = "farm_id", nullable = false, referencedColumnName = "farmID")
    private Farm farm;

    @NotNull(message = "Start Date can not be empty!")
    private LocalDateTime startDate;

    @NotNull(message = "End Date can not be empty!")
    private LocalDateTime endDate;

    @Column(nullable = false)
    @JsonIgnore
    private boolean status = true;
}

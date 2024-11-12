package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class TourSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "tour_id", nullable = false, referencedColumnName = "tourId")
    @JsonBackReference
    private Tour tour;

    @ManyToOne
    @JoinColumn(name = "farm_id", nullable = false, referencedColumnName = "farmId")
    @JsonBackReference
    private Farm farm;

    @Lob
    @NotBlank(message = "Mô tả lịch trình đi tour không được để trống!")
    private String scheduleDescription;
}

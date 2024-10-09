package com.project.KoiBookingSystem.entity;

<<<<<<< HEAD
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
=======
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
import java.time.LocalDateTime;

@Data
@Entity
public class TourSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
<<<<<<< HEAD
    @JoinColumn(name = "tour_id", nullable = false, referencedColumnName = "tourId")
    private Tour tour;

    @ManyToOne
    @JoinColumn(name = "farm_id", nullable = false, referencedColumnName = "farmId")
    private Farm farm;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;
=======
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
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
}

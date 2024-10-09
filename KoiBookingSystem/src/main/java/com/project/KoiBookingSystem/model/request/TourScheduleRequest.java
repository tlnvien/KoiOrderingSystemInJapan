package com.project.KoiBookingSystem.model.request;

<<<<<<< HEAD
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
=======
import com.project.KoiBookingSystem.entity.Farm;
import com.project.KoiBookingSystem.entity.Tour;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
import java.time.LocalDateTime;

@Data
public class TourScheduleRequest {

<<<<<<< HEAD
    @NotBlank(message = "Tour Farm can not be blank!")
    private String farmId;

    @NotNull(message = "Start Date can not be null")
    private LocalDateTime startDate;

    @NotNull(message = "Start Date can not be null")
    private LocalDateTime endDate;

=======
    private String farm;

    @NotNull(message = "Start Date can not be empty!")
    private LocalDateTime startDate;

    @NotNull(message = "End Date can not be empty!")
    private LocalDateTime endDate;
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
}

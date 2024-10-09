package com.project.KoiBookingSystem.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.KoiBookingSystem.entity.Account;
<<<<<<< HEAD
import com.project.KoiBookingSystem.enums.TourApproval;
import com.project.KoiBookingSystem.enums.TourType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
=======
import com.project.KoiBookingSystem.enums.TourType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec

@Data
public class TourRequest {

<<<<<<< HEAD
    @NotBlank(message = "Tour Name can not be empty!")
    private String tourName;

    @Min(value = 0, message = "Invalid Tour Max Participant")
    private int maxParticipants;

    @Min(value = 0, message = "Invalid Remain Seat!")
    private int remainSeat;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate departureDate;

    @NotBlank(message = "Duration can not be empty!")
    @Size(max = 4, message = "Duration can not exceed 4 characters!")
    @Pattern(regexp = "^\\d+N\\d+D$", message = "Invalid Tour Duration!")
    private String duration;
=======
    @NotBlank(message = "Tour ID can not be empty!")
    @Column(unique = true)
    private String tourID;

    @NotBlank(message = "Tour Name can not be empty!")
    private String tourName;

    @Column(nullable = false)
    @Min(value = 0, message = "Invalid Tour Max Participant")
    private int maxParticipants;

    @Column(nullable = false)
    @Min(value = 0, message = "Invalid Remain Seat!")
    private int remainSeat;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime departureDate;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec

    @NotBlank(message = "Tour Description can not be empty!")
    private String description;

    private String consulting;

    @Enumerated(EnumType.STRING)
    private TourType type;

    @Min(value = 0, message = "Invalid Price")
<<<<<<< HEAD
    private Double price;

    private List<TourScheduleRequest> tourSchedules;

=======
    private double price;

    private String manager;
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
}

package com.project.KoiBookingSystem.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.enums.TourApproval;
import com.project.KoiBookingSystem.enums.TourType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class TourRequest {

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

    @NotBlank(message = "Tour Description can not be empty!")
    private String description;

    private String consulting;

    @Enumerated(EnumType.STRING)
    private TourType type;

    @Min(value = 0, message = "Invalid Price")
    private Double price;

    private String tourImage;

    private List<TourScheduleRequest> tourSchedules;

}

package com.project.KoiBookingSystem.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.KoiBookingSystem.enums.BookingStatus;
import com.project.KoiBookingSystem.model.response.CustomerOfBookingResponse;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookingRequest {
    @JsonIgnore
    private String bookingID;


//    @NotBlank(message = "Customer ID can not be blank!!!")
//    private String customerID;


    @NotBlank(message = "Tour ID can not be blank!!!")
    private String tourID;

    @Min(value = 1, message = "Number of persons must be at least 1")
    private int numberOfPerson;


    private CustomerOfBookingResponse customer;


}

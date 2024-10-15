package com.project.KoiBookingSystem.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.KoiBookingSystem.enums.BookingStatus;
import com.project.KoiBookingSystem.enums.BookingType;
import com.project.KoiBookingSystem.model.response.UserResponse;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequest {
    @JsonIgnore
    private String bookingID;

    @NotBlank(message = "Customer ID can not be blank!!!")
    private String customerID;


    @NotBlank(message = "Tour ID can not be blank!!!")
    private String tourID;

    @NotNull(message = "Create date cannot be null")
    @PastOrPresent(message = "Create date cannot be in the future")
    private LocalDate createDate;

    @Min(value = 1, message = "Number of persons must be at least 1")
    private int numberOfPerson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Booking status cannot be null")
    private BookingStatus bookingStatus;

//    @JsonIgnore
//    private String paymentID;

    private UserResponse customer;

//    @Positive(message = "Total price must be positive")
//    private double totalPrice;


}

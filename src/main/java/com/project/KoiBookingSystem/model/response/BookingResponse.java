package com.project.KoiBookingSystem.model.response;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.KoiBookingSystem.enums.BookingStatus;
import com.project.KoiBookingSystem.enums.BookingType;
import com.project.KoiBookingSystem.enums.PaymentIsOver24H;
import com.project.KoiBookingSystem.enums.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingResponse {
    @NotBlank(message = "Booking ID cannot be null")
    private String bookingId;

    @NotNull(message = "Create date cannot be null")
    @PastOrPresent(message = "Create date cannot be in the future")
    private LocalDateTime createDate;

    @Min(value = 1, message = "Number of persons must be at least 1")
    private int numberOfPerson;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Booking status cannot be null")
    private BookingStatus bookingStatus;

    @Positive(message = "Total price must be positive")
    private float totalPrice;

    @Enumerated(EnumType.STRING)
    private BookingType bookingType;

    @NotNull(message = "seatBooked can not be null")
    private int seatBooked;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Booking status cannot be null")
    private PaymentIsOver24H paymentIsOver24H;


    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private CustomerOfBookingResponse customer;

    private BookingAvailableTour tourID;
}

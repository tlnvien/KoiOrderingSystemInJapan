package com.project.KoiBookingSystem.model.response;

import com.project.KoiBookingSystem.enums.BookingStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingResponse {

    private String bookingId;

    private String customerId;

    private String tourId;

    private String paymentId;

    private String description;

    private boolean hasVisa;

    private int numberOfAttendances;

    private LocalDateTime createdDate;

    private double totalPrice;

    private BookingStatus status;
}

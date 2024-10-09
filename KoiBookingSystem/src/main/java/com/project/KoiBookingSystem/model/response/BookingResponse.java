package com.project.KoiBookingSystem.model.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingResponse {

    private String bookingId;

    private String customerId;

    private String tourId;

    private String paymentId;

    private int numberOfAttendances;

    private LocalDateTime createdDate;
}

package com.project.KoiBookingSystem.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.KoiBookingSystem.enums.BookingStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
public class BookingResponse {

    private String bookingId;

    private String customerName;

    private String phone;

    private String tourId;

    private String paymentId;

    private String description;

    private boolean hasVisa;

    private int numberOfAttendances;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime checkingDate;

    private double totalPrice;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}

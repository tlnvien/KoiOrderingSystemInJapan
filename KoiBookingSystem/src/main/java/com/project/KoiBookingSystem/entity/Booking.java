package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.KoiBookingSystem.enums.BookingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    @Pattern(regexp = "^B\\d+$", message = "Invalid Booking ID")
    private String bookingId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false, referencedColumnName = "userId")
    private Account customer;

    @ManyToOne // one tour has many booking, a booking has a tour
    @JoinColumn(name = "tour_id", nullable = false, referencedColumnName = "tourId")
    private Tour tour;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;

    @Column(nullable = false)
    @Min(value = 0, message = "Invalid number of attendances")
    private int numberOfAttendances;

    @OneToOne
    @JoinColumn(name = "payment_id", nullable = false, referencedColumnName = "paymentId")
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;
}

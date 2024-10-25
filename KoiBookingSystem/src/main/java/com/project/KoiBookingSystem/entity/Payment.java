package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.KoiBookingSystem.enums.PaymentCurrency;
import com.project.KoiBookingSystem.enums.PaymentMethod;
import com.project.KoiBookingSystem.enums.PaymentStatus;
import com.project.KoiBookingSystem.enums.PaymentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    @Pattern(regexp = "PM\\d+", message = "Invalid Payment ID")
    private String paymentId;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    private PaymentCurrency currency;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private double price;

//    @ManyToOne
//    @JoinColumn(name = "tour_id", referencedColumnName = "tourId")
//    private Tour tour;

    @OneToOne
    @JoinColumn(name = "booking_id", referencedColumnName = "bookingId")
    @JsonBackReference
    private Booking booking;

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "orderId")
    @JsonBackReference
    private Orders orders;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<Transactions> transactions;
}

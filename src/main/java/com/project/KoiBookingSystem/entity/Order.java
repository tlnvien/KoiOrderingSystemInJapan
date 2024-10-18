package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orderId;

    @ManyToOne
    @JoinColumn(name = "customerId", referencedColumnName = "userID")
    @JsonIgnore
    private Account customer;

    @ManyToOne
    @JoinColumn(name = "bookingId", referencedColumnName = "bookingId")
    private Booking booking;

    private Date date;

    private float total;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<DetailOrder> DetailOrders;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "deliveringId", referencedColumnName = "userId")
    private Account delivering;
}

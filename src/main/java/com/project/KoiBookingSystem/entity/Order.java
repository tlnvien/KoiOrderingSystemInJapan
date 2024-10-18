package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.KoiBookingSystem.enums.OrderStatus;
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
    private String orderId;

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

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @PrePersist
    public void generateOrderId() {
        String uniqueId = UUID.randomUUID().toString(); // Tạo UUID
        this.orderId = "ORD" + uniqueId; // Kết hợp với tiền tố "ORD"
    }
}

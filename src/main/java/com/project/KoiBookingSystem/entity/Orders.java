package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.KoiBookingSystem.enums.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Orders {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String orderId;

    @ManyToOne
    @JoinColumn(name = "customerId", referencedColumnName = "userID")
    @JsonIgnore
    private Account customer;

    @ManyToOne
    @JoinColumn(name = "tour_id", nullable = false, referencedColumnName = "tourId")
    @JsonBackReference
    private Tour tour;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime orderDate;

    @Column(nullable = false)
    @Min(value = 0, message = "Price can not be lower than 0!")
    private float total;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;

    @ManyToOne
    @JoinColumn(name = "deliveringId", referencedColumnName = "userId")
    private Account delivering;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime deliveredDate;

    @Column(nullable = false)
    private double paidPrice;

    @Column(nullable = false)
    private double remainingPrice;

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<OrdersPayment> payments;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @PrePersist
    public void generateOrderId() {
        String uniqueId = UUID.randomUUID().toString(); // Tạo UUID
        this.orderId = "ORD" + uniqueId; // Kết hợp với prefix "ORD"
    }
}

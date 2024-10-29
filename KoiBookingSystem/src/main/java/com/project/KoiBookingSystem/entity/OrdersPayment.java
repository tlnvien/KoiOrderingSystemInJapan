package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.project.KoiBookingSystem.enums.OrderPaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class OrdersPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "orderId")
    @JsonBackReference
    private Orders orders;

    @OneToOne
    @JoinColumn(name = "payment_id", referencedColumnName = "paymentId")
    @JsonBackReference
    private Payment payment;

    @Enumerated(EnumType.STRING)
    private OrderPaymentStatus status;
}

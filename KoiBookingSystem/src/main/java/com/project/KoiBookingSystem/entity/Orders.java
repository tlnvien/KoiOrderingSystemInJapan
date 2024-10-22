package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.KoiBookingSystem.enums.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String orderId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false, referencedColumnName = "userId")
    @JsonBackReference
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
    private double totalPrice;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String note;

    @OneToOne(mappedBy = "orders", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Payment payment;

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<OrderDetail> orderDetails;

}

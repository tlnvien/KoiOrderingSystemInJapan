package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.KoiBookingSystem.enums.PaymentEnums;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Date createdAt;

    @Enumerated(EnumType.STRING)
    private PaymentEnums payment_method;


    @OneToOne
    @JoinColumn(name = "order")
    private CustomerOrder customerOrder;

    @OneToOne
    @JoinColumn(name = "payment_Id")
    @JsonIgnore // Ngăn vòng lặp
    private Booking booking;


    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Transactions> transactions;
}

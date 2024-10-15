package com.project.KoiBookingSystem.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.KoiBookingSystem.enums.TransactionEnums;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Transactions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @ManyToOne
    @JoinColumn(name = "from_id")
    private Account from;


    @ManyToOne
    @JoinColumn(name = "to_id")
    private Account to;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    @JsonIgnore
    private Payment payment;

    @Enumerated(EnumType.STRING)
    private TransactionEnums status;

    private String description;
}

package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.KoiBookingSystem.enums.TransactionsEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
public class Transactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "from_id", referencedColumnName = "userId")
    @JsonBackReference
    private Account fromAccount;

    @ManyToOne
    @JoinColumn(name = "to_id", referencedColumnName = "userId")
    @JsonBackReference
    private Account toAccount;

    @ManyToOne
    @JoinColumn(name = "payment_id", referencedColumnName = "paymentId")
    @JsonBackReference
    private Payment payment;

    private double amount;

    @Lob
    private String description;

    @Enumerated(EnumType.STRING)
    private TransactionsEnum status;

}

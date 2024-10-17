package com.project.KoiBookingSystem.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    private Date date;

    private float total;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "userID")
    @JsonIgnore
    private Account customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;


    @OneToOne(mappedBy = "orders", cascade = CascadeType.ALL)
    private Payment payment;
}

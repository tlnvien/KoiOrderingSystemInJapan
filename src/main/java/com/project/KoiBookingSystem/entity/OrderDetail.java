package com.project.KoiBookingSystem.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
@Entity
public class OrderDetail {
    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    private UUID id;


    private float price;

    private int quantity;


    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Orders order;

    @ManyToOne
    @JoinColumn(name = "koi_id", referencedColumnName = "koiID")
    private Koi koi;
}

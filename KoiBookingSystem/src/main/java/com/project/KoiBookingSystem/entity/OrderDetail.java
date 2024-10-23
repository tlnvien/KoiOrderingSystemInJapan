package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "orderId", nullable = false)
    @JsonBackReference
    private Orders orders;

    @ManyToOne
    @JoinColumn(name = "farm_id", referencedColumnName = "farmId", nullable = false)
    @JsonBackReference
    private Farm farms;

    @ManyToOne
    @JoinColumn(name = "koi_id", referencedColumnName = "koiId", nullable = false)
    @JsonBackReference
    private Koi koi;

    @Min(value = 0, message = "Invalid quantity!")
    private int quantity;

    @Min(value = 0, message = "Price can not be lower than 0!")
    private double price;

    @Lob
    private String description;
}

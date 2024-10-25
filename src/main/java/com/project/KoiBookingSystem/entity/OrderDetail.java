package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Entity
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private String id;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "farm_id", referencedColumnName = "farmId", nullable = false)
    @JsonBackReference
    private Farm farms;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "orderId")
    @JsonIgnore
    private Orders order;

    @ManyToOne
    @JoinColumn(name = "koiID", referencedColumnName = "koiID")
    private Koi koi;

    @Lob
    private String description;
}

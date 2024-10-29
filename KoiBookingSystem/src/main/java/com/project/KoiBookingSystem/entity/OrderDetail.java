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
    @JoinColumn(name = "koi_id", referencedColumnName = "koiId", nullable = false)
    @JsonBackReference
    private Koi koi;

    @Min(value = 0, message = "Số lượng cá Koi không hợp lệ!")
    private int quantity;

    @Min(value = 0, message = "Giá tiền không được phép dưới 0!")
    private double price;

    @Lob
    private String description;
}

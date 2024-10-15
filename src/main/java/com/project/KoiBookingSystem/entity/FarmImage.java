package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class FarmImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String imageLink;

    @Column(nullable = false)
    private boolean status;

    @ManyToOne
    @JoinColumn(name = "farm_id", nullable = false, referencedColumnName = "farmid")
    private Farm farm;
}

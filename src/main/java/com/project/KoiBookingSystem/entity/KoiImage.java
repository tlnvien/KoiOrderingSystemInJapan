package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class KoiImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private long id;

    private String imageLink;

    @Column(nullable = false)
    @JsonIgnore
    private boolean status;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "koi_id", nullable = false, referencedColumnName = "koiid")
    private Koi koi;
}

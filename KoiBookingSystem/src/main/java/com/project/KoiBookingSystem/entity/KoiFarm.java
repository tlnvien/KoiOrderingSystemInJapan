package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class KoiFarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "farm_id", nullable = false, referencedColumnName = "farmid")
    private Farm farm;

    @ManyToOne
    @JoinColumn(name = "koi_id", nullable = false, referencedColumnName = "koiid")
    private Koi koi;

    @Column(nullable = false)
    @JsonIgnore
    private LocalDateTime addedDate;

    private boolean status = true;
}

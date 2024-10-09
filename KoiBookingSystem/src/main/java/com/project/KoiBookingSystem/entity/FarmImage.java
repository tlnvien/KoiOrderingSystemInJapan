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
<<<<<<< HEAD
    @JoinColumn(name = "farm_id", nullable = false, referencedColumnName = "farmId")
=======
    @JoinColumn(name = "farm_id", nullable = false, referencedColumnName = "farmid")
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
    private Farm farm;
}

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
<<<<<<< HEAD
    @JoinColumn(name = "koi_id", nullable = false, referencedColumnName = "koiID")
=======
    @JoinColumn(name = "koi_id", nullable = false, referencedColumnName = "koiid")
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
    private Koi koi;
}

package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

<<<<<<< HEAD
import java.time.LocalDate;
=======
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
public class Koi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Koi ID can not be empty!")
    @Column(unique = true, nullable = false)
<<<<<<< HEAD
    private String koiId;
=======
    private String koiID;
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec

    @NotBlank(message = "Koi Species can not be empty!")
    private String species;

    @NotBlank(message = "Koi Description can not be empty!")
    private String description;

    @Column(nullable = false)
<<<<<<< HEAD
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdDate;
=======
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec

    @Column(nullable = false)
    private boolean status;

    @JsonManagedReference
    @OneToMany(mappedBy = "koi", cascade = CascadeType.ALL)
    private List<KoiImage> koiImages;

    @OneToMany(mappedBy = "koi", cascade = CascadeType.ALL)
<<<<<<< HEAD
    private Set<KoiFarm> koiFarms;

    @ManyToOne
    @JoinColumn(name = "manager_id", nullable = false, referencedColumnName = "userId")
=======
    private Set<KoiFarm> koiFarms = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "manager_id", nullable = false, referencedColumnName = "userID")
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
    private Account manager;
}

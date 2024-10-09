package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
<<<<<<< HEAD
=======
import com.fasterxml.jackson.annotation.JsonIgnore;
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

<<<<<<< HEAD
import java.time.LocalDate;
=======
import java.time.LocalDateTime;
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
public class Farm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Farm ID can not be empty!")
    @Column(unique = true, nullable = false)
<<<<<<< HEAD
    private String farmId;
=======
    private String farmID;
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec

    @NotBlank(message = "Farm Name can not be empty!")
    @Column(nullable = false)
    private String farmName;

    @NotBlank(message = "Farm Description can not be empty!")
    private String description;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
<<<<<<< HEAD
    private LocalDate createdDate;
=======
    private LocalDateTime createdDate;
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec

    @Column(nullable = false)
    private boolean status;

    @OneToMany(mappedBy = "farm", cascade = CascadeType.ALL) // các thuộc tính cần thiết để modify dữ liệu trong database
    private List<FarmImage> farmImages;

    @OneToMany(mappedBy = "farm", cascade = CascadeType.ALL)
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


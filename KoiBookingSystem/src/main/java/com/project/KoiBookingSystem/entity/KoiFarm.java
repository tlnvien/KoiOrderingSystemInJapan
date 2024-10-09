package com.project.KoiBookingSystem.entity;

<<<<<<< HEAD
import com.fasterxml.jackson.annotation.JsonFormat;
=======
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

<<<<<<< HEAD
import java.time.LocalDate;
=======
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
import java.time.LocalDateTime;

@Data
@Entity
<<<<<<< HEAD
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"farm_id", "koi_id"})})
=======
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
public class KoiFarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
<<<<<<< HEAD
    @JoinColumn(name = "farm_id", nullable = false, referencedColumnName = "farmId")
    private Farm farm;

    @ManyToOne
    @JoinColumn(name = "koi_id", nullable = false, referencedColumnName = "koiId")
=======
    @JoinColumn(name = "farm_id", nullable = false, referencedColumnName = "farmid")
    private Farm farm;

    @ManyToOne
    @JoinColumn(name = "koi_id", nullable = false, referencedColumnName = "koiid")
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
    private Koi koi;

    @Column(nullable = false)
    @JsonIgnore
<<<<<<< HEAD
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate addedDate;
=======
    private LocalDateTime addedDate;
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec

    private boolean status = true;
}

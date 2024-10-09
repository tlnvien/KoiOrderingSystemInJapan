package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
<<<<<<< HEAD
import com.project.KoiBookingSystem.enums.TourApproval;
import com.project.KoiBookingSystem.enums.TourType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
=======
import com.project.KoiBookingSystem.enums.TourType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec

@Data
@Entity
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Tour ID can not be empty!")
    @Column(unique = true)
<<<<<<< HEAD
    @Pattern(regexp = "^T\\d+$", message = "Invalid Tour ID")
    private String tourId;
=======
    private String tourID;
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec

    @NotBlank(message = "Tour Name can not be empty!")
    private String tourName;

    @Column(nullable = false)
    @Min(value = 0, message = "Invalid Tour Max Participant")
    private int maxParticipants;

    @Column(nullable = false)
    @Min(value = 0, message = "Invalid Remain Seat!")
    private int remainSeat;

    @Column(nullable = false)
<<<<<<< HEAD
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate departureDate;

    @NotBlank(message = "Duration can not be empty!")
    @Column(nullable = false)
    @Size(max = 4, message = "Duration can not exceed 4 characters!")
    @Pattern(regexp = "^\\d+N\\d+D$", message = "Invalid Tour Duration!")
    private String duration;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;
=======
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime departureDate;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec

    @NotBlank(message = "Tour Description can not be empty!")
    private String description;

    @ManyToOne
<<<<<<< HEAD
    @JoinColumn(name = "consulting_id", referencedColumnName = "userId", nullable = false)
=======
    @JoinColumn(name = "consulting_id", referencedColumnName = "userID", nullable = false)
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
    private Account consulting;

    @Enumerated(EnumType.STRING)
    private TourType type;

    @Column(nullable = false)
<<<<<<< HEAD
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdDate;
=======
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec

    @Min(value = 0, message = "Invalid Price")
    private double price;

<<<<<<< HEAD
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TourApproval tourApproval;

=======
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
    @Column(nullable = false)
    private boolean status;

    @ManyToOne(fetch = FetchType.LAZY)
<<<<<<< HEAD
    @JoinColumn(name = "sales_id", nullable = false, referencedColumnName = "userId")
    private Account sales;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    private List<TourSchedule> tourSchedules = new ArrayList<>();

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Booking> bookings;
=======
    @JoinColumn(name = "manager_id", nullable = false, referencedColumnName = "userID")
    private Account manager;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    private List<TourSchedule> tourSchedules;
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
}

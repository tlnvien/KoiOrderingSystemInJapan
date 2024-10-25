package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.KoiBookingSystem.enums.TourApproval;
//import com.project.KoiBookingSystem.enums.TourStatus;
import com.project.KoiBookingSystem.enums.TourType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Tour ID can not be empty!")
    @Column(unique = true)
    @Pattern(regexp = "^T\\d+$", message = "Invalid Tour ID")
    private String tourId;

    @NotBlank(message = "Tour Name can not be empty!")
    @Column(nullable = false, unique = true)
    private String tourName;

    @Column(nullable = false)
    @Min(value = 0, message = "Invalid Tour Max Participant")
    private int maxParticipants;

    @Column(nullable = false)
    @Min(value = 0, message = "Invalid Remain Seat!")
    private int remainSeat;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate departureDate;

    @NotBlank(message = "Duration can not be empty!")
    @Column(nullable = false)
    @Size(max = 4, message = "Duration can not exceed 4 characters!")
    @Pattern(regexp = "^\\d+N\\d+D$", message = "Invalid Tour Duration!")
    private String duration;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate endDate;

    @NotBlank(message = "Tour Description can not be empty!")
    @Lob
    private String description;

    @ManyToOne
    @JoinColumn(name = "consulting_id", referencedColumnName = "userId", nullable = false)
    @JsonBackReference
    private Account consulting;

    @Enumerated(EnumType.STRING)
    private TourType type;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdDate;

    @Min(value = 0, message = "Invalid Price")
    private double price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TourApproval tourApproval;

    @Lob
    private String tourImage; //

//    @Column(nullable = false)
//    @Enumerated(EnumType.STRING)
//    private TourStatus status;

    @ManyToOne
    @JoinColumn(name = "sales_id", nullable = false, referencedColumnName = "userId")
    @JsonBackReference
    private Account sales;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<TourSchedule> tourSchedules = new ArrayList<>();

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Booking> bookings;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    private Set<Orders> orders;
}

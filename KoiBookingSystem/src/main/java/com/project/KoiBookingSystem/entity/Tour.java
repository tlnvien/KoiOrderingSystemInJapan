package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

@Data
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
    private String tourName;

    @Column(nullable = false)
    @Min(value = 0, message = "Invalid Tour Max Participant")
    private int maxParticipants;

    @Column(nullable = false)
    @Min(value = 0, message = "Invalid Remain Seat!")
    private int remainSeat;

    @Column(nullable = false)
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

    @NotBlank(message = "Tour Description can not be empty!")
    private String description;

    @ManyToOne
    @JoinColumn(name = "consulting_id", referencedColumnName = "userId", nullable = false)
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

    @Column(nullable = false)
    private boolean status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_id", nullable = false, referencedColumnName = "userId")
    private Account sales;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    private List<TourSchedule> tourSchedules = new ArrayList<>();

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Booking> bookings;
}

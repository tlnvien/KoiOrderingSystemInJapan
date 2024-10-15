package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.KoiBookingSystem.enums.TourApproval;
import com.project.KoiBookingSystem.enums.TourType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
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
    private String tourID;

    @NotBlank(message = "Tour Name can not be empty!")
    private String tourName;

    @Column(nullable = false)
    @Min(value = 0, message = "Invalid Tour Max Participant")
    private int maxParticipants;
//
//    @Column(nullable = false)
//    @Min(value = 0, message = "Invalid Remain Seat!")
//    private int remainSeat;





    @Column(nullable = false)
    @Future(message = "Invalid Tour End Date!")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Column(nullable = false)
    @Future(message = "Invalid Tour End Date!")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @NotBlank(message = "Tour Description can not be empty!")
    private String description;

    @ManyToOne
    @JoinColumn(name = "consulting_id", referencedColumnName = "userID", nullable = false)
    private Account consulting;

    @Enumerated(EnumType.STRING)
    private TourType type;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdDate;

    @Min(value = 0, message = "Invalid Price")
    private float price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TourApproval tourApproval;

    @Column(nullable = false)
    private boolean status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_id", nullable = false, referencedColumnName = "userID")
    private Account sales;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    private List<TourSchedule> tourSchedules;

    @OneToMany(mappedBy = "tourId", cascade = CascadeType.ALL)
    private List<Booking> bookings;
}

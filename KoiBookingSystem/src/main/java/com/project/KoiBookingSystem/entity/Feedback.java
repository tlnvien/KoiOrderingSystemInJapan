package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    @Pattern(regexp = "^FB\\d+$", message = "Định dạng Id feedback không hợp lệ!")
    private String feedbackId;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "userId", nullable = false)
    @JsonBackReference
    private Account customer;

    @ManyToOne
    @JoinColumn(name = "tour_id", referencedColumnName = "tourId", nullable = false)
    private Tour tour;

    @Min(value = 1, message = "Rating không hợp lệ!")
    @Max(value = 5, message = "Rating không hợp lệ!")
    private int rating;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime feedbackDate;

    private String comments;

    @Column(nullable = false)
    private boolean status;
}

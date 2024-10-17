package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.project.KoiBookingSystem.enums.FeedbackType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    @Pattern(regexp = "^FB\\d+$", message = "Invalid Feedback ID!")
    private String feedbackId;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "userId", nullable = false)
    @JsonBackReference
    private Account customer;

    @ManyToOne
    @JoinColumn(name = "farm_id", referencedColumnName = "farmId")
    @JsonBackReference
    private Farm farm;

    @ManyToOne
    @JoinColumn(name = "koi_id", referencedColumnName = "koiId")
    @JsonBackReference
    private Koi koi;

    @ManyToOne
    @JoinColumn(name = "staff_id", referencedColumnName = "userId")
    @JsonBackReference
    private Account staff;

    @Min(value = 1, message = "Invalid rating!")
    @Max(value = 5, message = "Invalid rating!")
    private int rating;

    private String comments;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FeedbackType type;

    @Column(nullable = false)
    private boolean status;
}

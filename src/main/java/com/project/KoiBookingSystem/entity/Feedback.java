package com.project.KoiBookingSystem.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Feedback {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;

    private String feedbackComment;

    private Date feedbackDate;


    private int rating;

    @ManyToOne
    @JoinColumn (name = "customer_id")
    private Account customer;

    @ManyToOne
    @JoinColumn (name = "tour_Id", referencedColumnName = "tourID")
    private Tour tourFeedback;


    @ManyToOne
    @JoinColumn (name = "staff_Id")
    private Account staff;


}

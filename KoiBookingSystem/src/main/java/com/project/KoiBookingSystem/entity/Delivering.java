package com.project.KoiBookingSystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class Delivering {

    @Id
    private long id;

    private String deliverId;

    private List<Orders> orders;

    private Account delivering;

    private LocalDateTime deliverTime;

}

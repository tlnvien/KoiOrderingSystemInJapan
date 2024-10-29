package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.KoiBookingSystem.enums.DeliveringStatus;
import com.project.KoiBookingSystem.service.DeliveringService;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
public class Delivering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String deliveringId;

    @OneToMany(mappedBy = "delivering", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Orders> orders;

    @Lob
    private String information;

    @ManyToOne
    @JoinColumn(name = "staff_id", referencedColumnName = "userId", nullable = false)
    @JsonBackReference
    private Account deliveringStaff;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    @Column(nullable = false)
    private LocalDateTime deliverDate; // THỜI GIAN TẠO ĐƠN GIAO HÀNG

    @Enumerated(EnumType.STRING)
    private DeliveringStatus status;
}

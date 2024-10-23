package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.units.qual.C;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    @Pattern(regexp = "^RQ\\d+$", message = "Invalid request ID!")
    private String requestId;

    @Lob
    @NotBlank(message = "Request information can not be blank!")
    private String information;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate createdDate;

    private boolean done;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate completedDate;

    @ManyToOne
    @JoinColumn(name = "host_id", referencedColumnName = "userId", nullable = false)
    private Account farmHost;
}

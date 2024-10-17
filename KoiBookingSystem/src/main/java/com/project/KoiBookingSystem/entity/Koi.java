package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class Koi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Koi ID can not be empty!")
    @Column(unique = true, nullable = false)
    @Pattern(regexp = "K\\d+", message = "Invalid Koi ID!")
    private String koiId;

    @NotBlank(message = "Koi Species can not be empty!")
    @Column(nullable = false, unique = true)
    private String species;

    @NotBlank(message = "Koi Description can not be empty!")
    private String description;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdDate;

    @Column(nullable = false)
    private boolean status;

    @JsonManagedReference
    @OneToMany(mappedBy = "koi", cascade = CascadeType.ALL)
    private List<KoiImage> koiImages;

    @OneToMany(mappedBy = "koi", cascade = CascadeType.ALL)
    private Set<KoiFarm> koiFarms;

    @ManyToOne
    @JoinColumn(name = "manager_id", nullable = false, referencedColumnName = "userId")
    private Account manager;

    @OneToMany(mappedBy = "koi", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Feedback> feedbackKoi;
}

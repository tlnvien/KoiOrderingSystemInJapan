package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
public class Farm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Farm ID can not be empty!")
    @Column(unique = true, nullable = false)
    @Pattern(regexp = "F\\d+", message = "Invalid Farm ID!")
    private String farmId;

    @OneToOne
    @JoinColumn(name = "farm_host_id", nullable = false, referencedColumnName = "userId")
    @JsonBackReference
    private Account farmHost;

    @NotBlank(message = "Farm Name can not be empty!")
    @Column(nullable = false, unique = true)
    private String farmName;

    @NotBlank(message = "Farm Description can not be empty!")
    @Lob
    private String description;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate createdDate;

    @Column(nullable = false)
    private boolean status;

    @OneToMany(mappedBy = "farm", cascade = CascadeType.ALL)
    private List<FarmImage> farmImages;

    @OneToMany(mappedBy = "farm", cascade = CascadeType.ALL)
    private Set<KoiFarm> koiFarms;

    @ManyToOne
    @JoinColumn(name = "manager_id", nullable = false, referencedColumnName = "userId")
    private Account manager;

    @OneToMany(mappedBy = "farms", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<OrderDetail> orderDetails;

}


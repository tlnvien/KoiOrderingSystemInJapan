package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
public class Farm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Farm ID can not be empty!")
    @Column(unique = true, nullable = false)
    private String farmId;

    @NotBlank(message = "Farm Name can not be empty!")
    @Column(nullable = false)
    private String farmName;

    @NotBlank(message = "Farm Description can not be empty!")
    private String description;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDate createdDate;

    @Column(nullable = false)
    private boolean status;

    @OneToMany(mappedBy = "farm", cascade = CascadeType.ALL) // các thuộc tính cần thiết để modify dữ liệu trong database
    private List<FarmImage> farmImages;

    @OneToMany(mappedBy = "farm", cascade = CascadeType.ALL)
    private Set<KoiFarm> koiFarms;

    @ManyToOne
    @JoinColumn(name = "manager_id", nullable = false, referencedColumnName = "userId")
    private Account manager;
}


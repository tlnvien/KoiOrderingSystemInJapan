package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.KoiBookingSystem.enums.TourApproval;
import com.project.KoiBookingSystem.enums.TourStatus;
import com.project.KoiBookingSystem.enums.TourType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Id của tour không được để trống!")
    @Column(unique = true)
    @Pattern(regexp = "^T\\d+$", message = "Định dạng Id của tour không hợp lệ!")
    private String tourId;

    @NotBlank(message = "Tên tour không được để trống!")
    @Column(nullable = false, unique = true)
    private String tourName;

    @Column(nullable = false)
    @Min(value = 1, message = "Số lượng người tối đa không hợp lệ!")
    private int maxParticipants;

    @Column(nullable = false)
    @Min(value = 0, message = "Số chỗ còn lại không hợp lệ!")
    private int remainSeat;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate departureDate;

    @NotBlank(message = "Thời gian đi tour không được để trống!")
    @Column(nullable = false)
    @Pattern(regexp = "^\\d+N\\d+D$", message = "Định dạng thời gian đi tour không hợp lệ!")
    private String duration;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate endDate;

    @NotBlank(message = "Mô tả của tour không được để trống!")
    @Lob
    private String description;

    @ManyToOne
    @JoinColumn(name = "consulting_id", referencedColumnName = "userId", nullable = false)
    @JsonBackReference
    private Account consulting;

    @Enumerated(EnumType.STRING)
    private TourType type;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdDate;

    @Min(value = 0, message = "Giá tiền của tour không được phép dưới 0!")
    private double price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TourApproval tourApproval;

    @Lob
    private String tourImage; //

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TourStatus status;

    @ManyToOne
    @JoinColumn(name = "sales_id", nullable = false, referencedColumnName = "userId")
    @JsonBackReference
    private Account sales;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<TourSchedule> tourSchedules = new ArrayList<>();

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Booking> bookings;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    private Set<Orders> orders;
}

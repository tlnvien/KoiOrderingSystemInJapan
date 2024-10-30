package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.KoiBookingSystem.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class BookingDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false, referencedColumnName = "bookingId")
    @JsonBackReference
    private Booking booking;

    @NotBlank(message = "Tên đầy đủ không được để trống!")
    @Pattern(regexp = "^[\\p{L} ]+$", message = "Tên đầy đủ không được chứa số hoặc ký tự đặc biệt!")
    @Column(nullable = false)
    private String customerName;

    @NotNull(message = "Ngày tháng năm sinh không được để trống!")
    @Past(message = "Ngày tháng năm sinh không hợp lệ!")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Column(nullable = false)
    private LocalDate dob;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Pattern(regexp = "(84[35789]|0[35789])\\d{8}", message = "Định dạng số điện thoại không hợp lệ!")
    private String phone;
}


package com.project.KoiBookingSystem.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.KoiBookingSystem.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
public class BookingRequest {

    @Pattern(regexp = "^[\\p{L} ]+$", message = "Tên đầy đủ không được chứa số hoặc ký tự đặc biệt!")
    private String fullName;

    @Pattern(regexp = "(84[35789]\\d{8}|0[35789]\\d{8})", message = "Số điện thoại không hợp lệ!")
    private String phone;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dob;

    @Min(value = 1, message = "Số lượng người đi tour không hợp lệ!")
    private int numberOfAttendees;

    @Lob
    private String description;

    private boolean hasVisa;

}

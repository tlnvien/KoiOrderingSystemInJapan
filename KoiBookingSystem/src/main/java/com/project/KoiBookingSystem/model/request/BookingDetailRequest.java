package com.project.KoiBookingSystem.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.KoiBookingSystem.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingDetailRequest {

    @NotBlank(message = "Tên đầy đủ không được để trống!")
    @Pattern(regexp = "^[\\p{L} ]+$", message = "Tên đầy đủ không được chứa số hoặc ký tự đặc biệt!")
    private String customerName;

    @NotNull(message = "Ngày tháng năm sinh không được để trống!")
    @Past(message = "Ngày tháng năm sinh không hợp lệ!")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dob;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Pattern(regexp = "(84[35789]|0[35789])\\d{8}", message = "Định dạng số điện thoại không hợp lệ!")
    private String phone;
}

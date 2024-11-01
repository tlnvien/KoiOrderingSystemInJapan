package com.project.KoiBookingSystem.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.KoiBookingSystem.enums.Gender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerOfBookingResponse {
    @Pattern(regexp = "^[\\p{L} ]+$", message = "Full name can not contain numbers or special characters")
    private String fullName;

    @Pattern(regexp = "(84[35789]\\d{8}|0[35789]\\d{8})", message = "Invalid phone number")
    private String phone;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @NotNull(message = "Ngày tháng năm sinh không được để trống!")
    @Past(message = "Ngày tháng năm sinh không hợp lệ!")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dob;

}

package com.project.KoiBookingSystem.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.KoiBookingSystem.enums.Gender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserRequest {

    @Pattern(regexp = "(84[35789]|0[35789])\\d{8}", message = "Invalid phone number")
    private String phone;

    @Pattern(regexp = "^[\\p{L} ]+$", message = "Full name can not contain numbers or special characters")
    private String fullName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Past(message = "Invalid date of birth")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dob;

    private String address;

    private String note;
}

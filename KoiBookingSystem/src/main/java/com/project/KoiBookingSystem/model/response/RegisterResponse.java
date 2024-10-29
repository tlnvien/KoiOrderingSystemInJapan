package com.project.KoiBookingSystem.model.response;

import com.project.KoiBookingSystem.enums.Gender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Data
public class RegisterResponse {

    private String userId;
    private String username;
    private String phone;
    private String email;
    private String fullName;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private LocalDate dob;
    private String address;
    private String note;
}

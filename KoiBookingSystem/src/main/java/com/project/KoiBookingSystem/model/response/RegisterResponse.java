package com.project.KoiBookingSystem.model.response;

import com.project.KoiBookingSystem.enums.Gender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.util.Date;

@Data
public class RegisterResponse {

    private String userId;
    private String username;
    private String phone;
    private String email;
    private String firstName;
    private String lastName;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private Date dob;
    private String address;
    private String note;
}

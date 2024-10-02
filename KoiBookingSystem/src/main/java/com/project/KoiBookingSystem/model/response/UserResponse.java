package com.project.KoiBookingSystem.model.response;

import com.project.KoiBookingSystem.enums.Gender;
import com.project.KoiBookingSystem.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.util.Date;

@Data
public class UserResponse {
    private String userID;
    private String username;
    private String phone;
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String firstName;
    private String lastName;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private Date dob;
    private String address;
    private String citizenID;
    private String note;
}

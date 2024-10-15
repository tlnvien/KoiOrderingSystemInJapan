package com.project.KoiBookingSystem.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.KoiBookingSystem.enums.Gender;
import com.project.KoiBookingSystem.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.Date;

@Data
public class UserResponse {

    private String userID;

    private String username;

    @JsonIgnore
    private String phone;
    @JsonIgnore
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @JsonIgnore
    private String firstName;

    @JsonIgnore
    private String lastName;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private Gender gender;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonIgnore
    private LocalDate dob;

    @JsonIgnore
    private String address;

    @JsonIgnore
    private String citizenID;
    @JsonIgnore
    private String note;
}

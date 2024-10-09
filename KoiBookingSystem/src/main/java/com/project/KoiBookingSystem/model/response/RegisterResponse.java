package com.project.KoiBookingSystem.model.response;

import com.project.KoiBookingSystem.enums.Gender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.util.Date;

@Data
public class RegisterResponse {

<<<<<<< HEAD
    private String userId;
=======
    private String userID;
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
    private String username;
    private String phone;
    private String email;
    private String firstName;
    private String lastName;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private Date dob;
    private String address;
<<<<<<< HEAD
    private String note;
=======
    private String citizenID;
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
}

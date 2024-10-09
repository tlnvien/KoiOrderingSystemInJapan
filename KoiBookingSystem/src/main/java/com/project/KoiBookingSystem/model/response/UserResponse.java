package com.project.KoiBookingSystem.model.response;

<<<<<<< HEAD
import com.fasterxml.jackson.annotation.JsonFormat;
=======
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
import com.project.KoiBookingSystem.enums.Gender;
import com.project.KoiBookingSystem.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
<<<<<<< HEAD
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
=======

>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
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
<<<<<<< HEAD

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dob;
    private String address;
=======
    private Date dob;
    private String address;
    private String citizenID;
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
    private String note;
}

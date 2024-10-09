package com.project.KoiBookingSystem.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.KoiBookingSystem.enums.Gender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRequest {

<<<<<<< HEAD
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "First name can not contain number")
    private String firstName;

    @Pattern(regexp = "^[a-zA-Z ]+$", message = "Last name can not contain number")
=======
    @Pattern(regexp = "^[a-zA-Z]+$", message = "First name can not contain number")
    private String firstName;

    @Pattern(regexp = "^[a-zA-Z]+$", message = "Last name can not contain number")
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Past(message = "Invalid date of birth")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dob;

    private String address;

<<<<<<< HEAD
=======
    @Pattern(regexp = "^0\\d{11}$", message = "Invalid citizen ID")
    private String citizenID;

>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
    private String note;
}

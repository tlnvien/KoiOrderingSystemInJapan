package com.project.KoiBookingSystem.model.response;

import com.project.KoiBookingSystem.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class LoginResponse {

    private String username;
<<<<<<< HEAD
    private String userId;
=======
    private String userID;
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
    private String phone;
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String token;
}

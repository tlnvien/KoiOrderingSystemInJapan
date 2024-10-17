package com.project.KoiBookingSystem.model.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Username can not be empty!")
    private String username;

    @NotBlank(message = "Password can not be empty!")
    @Size(min = 6, message = "Password must be at least 6 characters!")
    private String password;

    @Pattern(regexp = "(84[35789]|0[35789])\\d{8}", message = "Invalid phone number")
    @Column(unique = true)
    private String phone;

    @NotBlank(message = "Email can not be empty")
    @Email(message = "Invalid email!")
    private String email;
}

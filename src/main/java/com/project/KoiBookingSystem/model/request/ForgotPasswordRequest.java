package com.project.KoiBookingSystem.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequest {

    @NotBlank(message = "Email can not be empty!")
    @Email(message = "Invalid email!")
    private String email;
}

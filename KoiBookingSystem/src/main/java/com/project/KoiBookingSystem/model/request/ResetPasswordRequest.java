package com.project.KoiBookingSystem.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {

    @NotBlank(message = "Password can not be empty!")
    @Size(min = 6, message = "Password must be at least 6 characters!")
    private String password;
}

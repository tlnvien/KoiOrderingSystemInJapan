package com.project.KoiBookingSystem.model.response;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CustomerOfBookingResponse {
    @Pattern(regexp = "^[\\p{L} ]+$", message = "Full name can not contain numbers or special characters")
    private String fullName;

    @Pattern(regexp = "(84[35789]\\d{8}|0[35789]\\d{8})", message = "Invalid phone number")
    private String phone;
}

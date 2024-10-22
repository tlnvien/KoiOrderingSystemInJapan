package com.project.KoiBookingSystem.model.response;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CustomerOfBookingResponse {

    @Pattern(regexp = "^[a-zA-Z ]+$", message = "First name can not contain number")
    private String fullName;

    private String phone;
}

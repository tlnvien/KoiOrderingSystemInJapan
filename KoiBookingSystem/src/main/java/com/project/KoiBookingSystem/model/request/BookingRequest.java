package com.project.KoiBookingSystem.model.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingRequest {

    @Pattern(regexp = "^[a-zA-Z ]+$", message = "First name can not contain number")
    private String fullName;

    @Pattern(regexp = "(84[35789]\\d{8}|0[35789]\\d{8})", message = "Invalid phone number")
    private String phone;

    @Min(value = 0, message = "Invalid number of attendances")
    private int numberOfAttendees;

    private String description;

    private boolean hasVisa;

}

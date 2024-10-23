package com.project.KoiBookingSystem.model.request;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingRequest {

    @Pattern(regexp = "^[\\p{L} ]+$", message = "Full name can not contain numbers or special characters")
    private String fullName;

    @Pattern(regexp = "(84[35789]\\d{8}|0[35789]\\d{8})", message = "Invalid phone number")
    private String phone;

    @Min(value = 1, message = "Invalid number of attendances")
    private int numberOfAttendees;

    @Lob
    private String description;

    private boolean hasVisa;

}

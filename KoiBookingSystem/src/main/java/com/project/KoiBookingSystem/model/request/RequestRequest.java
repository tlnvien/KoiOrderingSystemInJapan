package com.project.KoiBookingSystem.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.KoiBookingSystem.entity.Account;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestRequest {

    @NotBlank(message = "First Name can not be blank!")
    @Pattern(regexp = "[a-zA-Z ]", message = "Invalid First Name!")
    private String firstName;

    @NotBlank(message = "First Name can not be blank!")
    @Pattern(regexp = "[a-zA-Z ]", message = "Invalid Last Name!")
    private String lastName;

    @Pattern(regexp = "(84|0[3|5|7|8|9])\\d{8}", message = "Invalid phone number")
    private String phone;

    @Min(value = 0, message = "Invalid Number of Participants")
    private Integer numberOfParticipants;

    private String textBox;

    private boolean visaCheck;

}

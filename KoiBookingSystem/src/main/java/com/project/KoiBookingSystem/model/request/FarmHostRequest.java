package com.project.KoiBookingSystem.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FarmHostRequest {

    @NotBlank(message = "Request Information can not be blank!")
    private String information;
}

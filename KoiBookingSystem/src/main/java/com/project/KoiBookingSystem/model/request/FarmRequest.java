package com.project.KoiBookingSystem.model.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FarmRequest {

    @NotBlank(message = "Farm Name can not be empty!")
    private String farmName;

    @NotBlank(message = "Farm Description can not be empty!")
    private String description;

    private List<FarmImageRequest> imageLinks;
}

package com.project.KoiBookingSystem.model.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class KoiRequest {

    @NotBlank(message = "Koi Species can not be empty!")
    private String species;

    @NotBlank(message = "Koi Description can not be empty!")
    private String description;

    private List<KoiImageRequest> imageLinks;
}

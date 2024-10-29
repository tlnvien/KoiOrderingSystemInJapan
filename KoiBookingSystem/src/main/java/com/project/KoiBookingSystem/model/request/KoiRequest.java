package com.project.KoiBookingSystem.model.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class KoiRequest {

    @NotBlank(message = "Giống cá không được phép để trống!")
    private String species;

    @NotBlank(message = "Mô tả của cá Koi không được để trống!")
    private String description;

    private List<KoiImageRequest> imageLinks;
}

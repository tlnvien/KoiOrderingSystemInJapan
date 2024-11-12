package com.project.KoiBookingSystem.model.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class FarmRequest {

    @NotBlank(message = "Tên trang trại không được để trống!")
    private String farmName;

    @NotBlank(message = "Chủ trang trại không được để trống!")
    private String farmHostId;

    @NotBlank(message = "Mô tả trang trại không được để trống!")
    private String description;

    private List<FarmImageRequest> imageLinks;
}

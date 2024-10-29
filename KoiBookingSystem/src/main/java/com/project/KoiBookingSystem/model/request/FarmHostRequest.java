package com.project.KoiBookingSystem.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FarmHostRequest {

    @NotBlank(message = "Thông tin của yêu cầu không được bỏ trống!")
    private String information;
}

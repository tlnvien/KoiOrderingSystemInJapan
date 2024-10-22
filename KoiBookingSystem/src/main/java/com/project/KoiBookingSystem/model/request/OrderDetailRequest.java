package com.project.KoiBookingSystem.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderDetailRequest {

    @NotBlank(message = "Farm Id can not be blank!")
    private String farmId;

    @NotBlank(message = "Koi Id can not be blank!")
    private String koiId;

    private String description;

    @Min(value = 0, message = "Invalid quantity!")
    private int quantity;

    @Min(value = 0, message = "Price can not be lower than 0!")
    private double price;
}

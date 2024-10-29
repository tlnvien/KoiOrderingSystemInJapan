package com.project.KoiBookingSystem.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderDetailRequest {

    @NotBlank(message = "Id của cá Koi không được để trống!")
    private String koiId;

    private String description;

    @Min(value = 0, message = "Số lượng cá Koi không hợp lệ!")
    private int quantity;

    @Min(value = 0, message = "Giá tiền không được phép nhỏ hơn 0!")
    private double price;
}

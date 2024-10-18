package com.project.KoiBookingSystem.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderDetailRequest {

//    private String orderId;

    @NotNull(message = "famrId can not be null!")
    private String farmId;

    @NotNull(message = "koiId can not be null!")
    private String koiId;

    @Min(value = 0, message = "quantity must be greater than 0")
    @NotNull(message = "quantity can not be null!")
    private int quantity;

    @Min(value = 0, message = "price must be greater than 0")
    @NotNull(message = "price can not be null")
    private int price;
}

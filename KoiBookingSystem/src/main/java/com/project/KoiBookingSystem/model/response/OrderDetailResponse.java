package com.project.KoiBookingSystem.model.response;

import lombok.Data;

@Data
public class OrderDetailResponse {

    private String koiId;

    private String description;

    private int quantity;

    private double price;
}

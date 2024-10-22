package com.project.KoiBookingSystem.model.response;

import lombok.Data;

@Data
public class OrderDetailResponse {

    private String farmId;

    private String koiId;

    private int quantity;

    private double price;
}

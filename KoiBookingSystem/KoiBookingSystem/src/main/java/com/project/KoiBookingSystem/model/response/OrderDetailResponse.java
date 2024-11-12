package com.project.KoiBookingSystem.model.response;

import lombok.Data;

@Data
public class OrderDetailResponse {

    private String species;

    private String description;

    private int quantity;

    private String price;
}

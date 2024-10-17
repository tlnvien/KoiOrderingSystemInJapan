package com.project.KoiBookingSystem.model.request;

import lombok.Data;

import java.util.UUID;
@Data
public class OrderDetailRequest {

    private UUID koiID;

    private int quantity;

}

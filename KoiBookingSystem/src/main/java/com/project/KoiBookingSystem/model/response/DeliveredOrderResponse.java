package com.project.KoiBookingSystem.model.response;

import lombok.Data;

@Data
public class DeliveredOrderResponse {

    private String orderId;

    private String fullName;

    private String phone;
}

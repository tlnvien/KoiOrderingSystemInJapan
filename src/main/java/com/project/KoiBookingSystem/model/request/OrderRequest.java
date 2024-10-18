package com.project.KoiBookingSystem.model.request;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OrderRequest {
    @NotNull(message = "userId can not be null")
    private String userId;

    @NotNull(message = "bookingId can not be null")
    private String bookingId;

//    @Enumerated(EnumType.STRING)
//    private String status;

    @NotNull(message = "product can not be null!")
    List<OrderDetailRequest> orderDetailRequests = new ArrayList<>();
}

package com.project.KoiBookingSystem.model.request;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class OrderRequest {

    @NotBlank(message = "Id của trang trại không được để trống!")
    private String farmId;

    private String description;

    private String note;

    private String customerAddress;

    private List<OrderDetailRequest> orderDetails;
}

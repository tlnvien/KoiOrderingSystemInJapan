package com.project.KoiBookingSystem.model.request;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

@Data
public class OrderRequest {

    private String description;

    private String note;

    private Set<OrderDetailRequest> orderDetails;
}

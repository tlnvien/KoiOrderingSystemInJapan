package com.project.KoiBookingSystem.model.request;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {

   private List<OrderDetailRequest> detail; // trong đây là cái list koiId, và quantity
}

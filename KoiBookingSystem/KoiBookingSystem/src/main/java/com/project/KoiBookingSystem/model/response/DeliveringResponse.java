package com.project.KoiBookingSystem.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.KoiBookingSystem.entity.Orders;
import com.project.KoiBookingSystem.enums.DeliveringStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DeliveringResponse {

    private String deliveringId;

    private String deliveringStaffId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime deliverDate;

    private String information;

    @Enumerated(EnumType.STRING)
    private DeliveringStatus status;

    private List<DeliveredOrderResponse> orderResponses;
}

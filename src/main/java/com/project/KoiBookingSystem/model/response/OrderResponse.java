package com.project.KoiBookingSystem.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.entity.Booking;
import com.project.KoiBookingSystem.entity.DetailOrder;
import com.project.KoiBookingSystem.entity.Payment;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class OrderResponse {
    @NotNull(message = "orderId can not be null!")
    private String orderId;

    @NotNull(message = "")
    private String customerName;

    @NotNull(message = "bookingId can not be null!")
    private Booking bookingId;

    @NotNull(message = "date can not be null!")
    @PastOrPresent(message = "Create date cannot be in the future")
    private Date date;

    @NotNull(message = "total price can not be null!")
    @Min(value = 0, message = "total price must be greater than 0")
    private float total;

    private List<DetailOrder> DetailOrders;

    private Payment payment;

    @NotNull(message = "delivering name can not be null")
    private String deliveringName;
}

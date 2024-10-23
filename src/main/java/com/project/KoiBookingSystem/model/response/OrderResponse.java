package com.project.KoiBookingSystem.model.response;

import com.project.KoiBookingSystem.entity.OrderDetail;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class OrderResponse {
    @NotNull(message = "orderId can not be null!")
    private String orderId;

    @NotNull(message = "FirstName of Customer can not be null!")
    private String customerFirstName;

    @NotNull(message = "LastName of Customer can not be null!")
    private String customerLastName;

    @NotNull(message = "bookingId can not be null!")
    private String bookingId;

    @NotNull(message = "date can not be null!")
    @PastOrPresent(message = "Create date cannot be in the future")
    private Date date;

    @NotNull(message = "total price can not be null!")
    @Min(value = 0, message = "total price must be greater than 0")
    private float total;

    private List<OrderDetail> orderDetails;

//    private Payment payment;

    @NotNull(message = "delivering name can not be null")
    private String deliveringName;
}

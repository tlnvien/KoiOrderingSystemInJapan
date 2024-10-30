package com.project.KoiBookingSystem.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.KoiBookingSystem.model.response.CustomerOfBookingResponse;
import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class BookingAvailableRequest {
    @JsonIgnore
    private String bookingID;


    @NotBlank(message = "Tour ID can not be blank!!!")
    private String tourID;

    @Column(nullable = false)
    @Min(value = 1, message = "Please enter the number of participants again!!! ")
    private int numberOfAttendances;

    private boolean hasVisa;

    // Thay đổi thành danh sách khách hàng
    @NotEmpty(message = "Customer list cannot be empty")
    private List<@Valid CustomerOfBookingResponse> customers;
}

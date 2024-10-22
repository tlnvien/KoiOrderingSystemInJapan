package com.project.KoiBookingSystem.model.request;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.KoiBookingSystem.model.response.CustomerOfBookingResponse;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class BookingAvailableRequest {
    @JsonIgnore
    private String bookingID;

//    @NotBlank(message = "Customer ID can not be blank!!!")
//    private String customerID;.

    @NotBlank(message = "Tour ID can not be blank!!!")
    private String tourID;

    @Column(nullable = false)
    @Min(value = 0, message = "Invalid number of attendances")
    private int numberOfAttendances;

    private boolean hasVisa;

    private CustomerOfBookingResponse customer;
    
}


package com.project.KoiBookingSystem.model.response;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.entity.Booking;
import com.project.KoiBookingSystem.entity.Delivering;
import com.project.KoiBookingSystem.entity.Orders;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class EmailDetail {

    private Account account;
    private String subject;
    private String link;
    private Booking booking;
    private Orders order;
    private String code;

}

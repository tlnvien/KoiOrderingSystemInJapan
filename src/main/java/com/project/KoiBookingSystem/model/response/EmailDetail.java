package com.project.KoiBookingSystem.model.response;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.entity.Booking;
import lombok.Data;

@Data
public class EmailDetail {
    private Account account;
    private String subject;
    private String link;
    private Booking booking;
    private String code;
}

package com.project.KoiBookingSystem.model.response;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.entity.Booking;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailDetail {

    private Account account;
    private String subject;
    private String link;
    private Booking booking;
    private String code;

    // gửi mail cho cái order confirmation thì thêm cái order vào đây
}

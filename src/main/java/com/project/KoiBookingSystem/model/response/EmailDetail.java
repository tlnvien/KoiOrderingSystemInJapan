package com.project.KoiBookingSystem.model.response;

import com.project.KoiBookingSystem.entity.Account;
import lombok.Data;

@Data
public class EmailDetail {

    private Account account;
    private String subject;
    private String link;
}

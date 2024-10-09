package com.project.KoiBookingSystem.model.response;

import lombok.Data;

@Data
public class RequestResponse {

    private String requestId;

    private String firstName;

    private String lastName;

    private String phone;

    private Integer numberOfParticipants;

    private String textBox;

    private boolean visaCheck;

    private String userId;

    private String salesId;
}

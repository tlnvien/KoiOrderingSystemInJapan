package com.project.KoiBookingSystem.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FarmHostResponse {

    private String requestId;

    private String information;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate createdDate;

    private String farmHostId;

    private boolean done;
}

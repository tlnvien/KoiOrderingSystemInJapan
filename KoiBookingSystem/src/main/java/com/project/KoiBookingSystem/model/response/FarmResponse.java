package com.project.KoiBookingSystem.model.response;

import lombok.Data;

import java.util.List;

@Data
public class FarmResponse {

    private String farmId;

    private String farmName;

    private String description;

    private List<String> imageLinks;

}

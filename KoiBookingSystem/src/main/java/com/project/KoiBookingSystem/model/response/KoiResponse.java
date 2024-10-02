package com.project.KoiBookingSystem.model.response;

import lombok.Data;

import java.util.List;

@Data
public class KoiResponse {

    private String koiID;

    private String species;

    private String description;

    private List<String> imageLinks;
}

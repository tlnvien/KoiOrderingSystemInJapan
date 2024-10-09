package com.project.KoiBookingSystem.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.List;

@Data
public class KoiResponse {

    private String koiId;

    private String species;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    private List<String> imageLinks;
}

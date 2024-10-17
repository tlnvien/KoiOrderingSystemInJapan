package com.project.KoiBookingSystem.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class KoiResponse {

    private String koiId;

    private String species;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    private List<KoiImageResponse> imageLinks;
}

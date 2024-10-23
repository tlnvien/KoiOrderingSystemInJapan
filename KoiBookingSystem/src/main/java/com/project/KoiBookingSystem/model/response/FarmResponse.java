package com.project.KoiBookingSystem.model.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FarmResponse {

    private String farmId;

    private String farmName;

    private String farmHostId;

    private String description;

    private List<FarmImageResponse> imageLinks;

}

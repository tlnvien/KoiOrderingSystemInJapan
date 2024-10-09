package com.project.KoiBookingSystem.model.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class FarmRequest {
    @Column(unique = true, nullable = false)
<<<<<<< HEAD
    private String farmId;
=======
    private String farmID;
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec

    @NotBlank(message = "Farm Name can not be empty!")
    private String farmName;

    @NotBlank(message = "Farm Description can not be empty!")
    private String description;

    private List<String> imageLinks;
}

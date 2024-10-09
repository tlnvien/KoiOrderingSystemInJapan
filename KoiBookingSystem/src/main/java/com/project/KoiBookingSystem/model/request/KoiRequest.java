package com.project.KoiBookingSystem.model.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class KoiRequest {
    @Column(unique = true, nullable = false)
<<<<<<< HEAD
    private String koiId;
=======
    private String koiID;
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec

    @NotBlank(message = "Koi Species can not be empty!")
    private String species;

    @NotBlank(message = "Koi Description can not be empty!")
    private String description;

    private List<String> imageLinks;
}

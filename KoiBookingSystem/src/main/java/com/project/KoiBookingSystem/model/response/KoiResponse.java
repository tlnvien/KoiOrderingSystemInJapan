package com.project.KoiBookingSystem.model.response;

<<<<<<< HEAD
import com.fasterxml.jackson.annotation.JsonFormat;
=======
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
import lombok.Data;

import java.util.List;

@Data
public class KoiResponse {

<<<<<<< HEAD
    private String koiId;
=======
    private String koiID;
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec

    private String species;

    private String description;

<<<<<<< HEAD
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
=======
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
    private List<String> imageLinks;
}

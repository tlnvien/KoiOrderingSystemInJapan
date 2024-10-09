package com.project.KoiBookingSystem.model.response;

<<<<<<< HEAD
import com.fasterxml.jackson.annotation.JsonFormat;
=======
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
import com.project.KoiBookingSystem.entity.Farm;
import com.project.KoiBookingSystem.entity.Tour;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

<<<<<<< HEAD
import java.time.LocalDate;
=======
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
import java.time.LocalDateTime;

@Data
public class TourScheduleResponse {
<<<<<<< HEAD

    private String farmId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
=======
    private Tour tour;

    private Farm farm;

    private LocalDateTime startDate;

>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
    private LocalDateTime endDate;
}

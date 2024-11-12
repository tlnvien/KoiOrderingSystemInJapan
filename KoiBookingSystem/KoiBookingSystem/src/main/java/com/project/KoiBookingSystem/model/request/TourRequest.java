package com.project.KoiBookingSystem.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.enums.TourApproval;
import com.project.KoiBookingSystem.enums.TourType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TourRequest {

    @NotBlank(message = "Tên tour không được để trống!")
    private String tourName;

    @Min(value = 1, message = "Số lượng người tối đa của tour không hợp lệ!")
    private int maxParticipants;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate departureDate;

    @NotBlank(message = "Thời gian đi tour không được để trống!")
    @Pattern(regexp = "^\\d+N\\d+D$", message = "Thời gian đi tour không hợp lệ!")
    private String duration;

    @NotBlank(message = "Mô tả của tour không được để trống!")
    private String description;

    private String consulting;

    @Min(value = 0, message = "Giá tiền của tour không hợp lệ!")
    private Double price;

    private String tourImage;

    private List<TourScheduleRequest> tourSchedules;

}

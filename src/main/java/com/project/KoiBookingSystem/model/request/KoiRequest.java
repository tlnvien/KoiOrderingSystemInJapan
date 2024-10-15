package com.project.KoiBookingSystem.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.KoiBookingSystem.entity.Account;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class KoiRequest {
    @Column(unique = true, nullable = false)
    private String koiID;

    @NotBlank(message = "Koi Species can not be empty!")
    private String species;

    @NotBlank(message = "Koi Description can not be empty!")
    private String description;

    private List<String> imageLinks;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "owner_id")
    private Account account;
}

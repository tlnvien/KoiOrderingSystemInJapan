package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.KoiBookingSystem.enums.RequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    @Pattern(regexp = "R\\d+", message = "Invalid Request ID")
    private String requestId;

    @NotBlank(message = "First Name can not be blank!")
    @Pattern(regexp = "[a-zA-Z ]", message = "Invalid First Name!")
    private String firstName;

    @NotBlank(message = "First Name can not be blank!")
    @Pattern(regexp = "[a-zA-Z ]", message = "Invalid Last Name!")
    private String lastName;

    @Pattern(regexp = "(84|0[3|5|7|8|9])\\d{8}", message = "Invalid phone number")
    @Column(unique = true, nullable = false)
    private String phone;

    @Column(nullable = false)
    @Min(value = 0, message = "Invalid Number of Participants")
    private Integer numberOfParticipants;

    private String textBox;

    @Column(nullable = false)
    private boolean visaCheck;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime createdDate;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false, referencedColumnName = "userId")
    private Account customer; // người khách hàng gửi yêu cầu lên hệ thống

    @ManyToOne
    @JoinColumn(name = "sales_id", referencedColumnName = "userId")
    private Account sales;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

}

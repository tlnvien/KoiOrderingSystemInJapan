package com.project.KoiBookingSystem.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.KoiBookingSystem.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterStaffRequest {

    @NotBlank(message = "Tên đăng nhập không được để trống!")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống!")
    @Size(min = 6, message = "Mật khẩu phải chứa ít nhất 6 ký tự!")
    private String password;

    @Pattern(regexp = "(84[35789]|0[35789])\\d{8}", message = "Định dạng số điện thoại không hợp lệ!")
    @Column(unique = true)
    @NotBlank(message = "Nhân viên không được để trống số điện thoại!")
    private String phone;

    @NotBlank(message = "Email không được để trống!")
    @Email(message = "Định dạng email không hợp lệ!")
    private String email;

    @Pattern(regexp = "^[\\p{L} ]+$", message = "Tên đầy đủ không được chứa số hoặc ký tự đặc biệt!")
    @NotBlank(message = "Nhân viên không được bỏ trống tên đầy đủ!")
    private String fullName;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Nhân viên phải cung cấp giới tính của mình!")
    private Gender gender;

    @Past(message = "Ngày tháng năm sinh không hợp lệ")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @NotNull(message = "Nhân viên không được để trống ngày tháng năm sinh")
    private LocalDate dob;

}

package com.project.KoiBookingSystem.model.request;

import com.project.KoiBookingSystem.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

// Thông tin hiển thị bên phía của người dùng khi đăng ký tài khoản (phiên bản rút gọn của account)
@Data
public class RegisterRequest {

    @NotBlank(message = "Username can not be empty!")
<<<<<<< HEAD
=======
    @Column(unique = true, nullable = false)
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
    private String username;

    @NotBlank(message = "Password can not be empty!")
    @Size(min = 6, message = "Password must be at least 6 characters!")
<<<<<<< HEAD
    private String password;

    @Pattern(regexp = "(84|0[3|5|7|8|9])\\d{8}", message = "Invalid phone number")
=======
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Phone number can not be empty!")
    @Pattern(regexp = "(84|0[3|5|7|8|9])\\d{8}", message = "Invalid phone number")
    @Column(unique = true, nullable = false)
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
    private String phone;

    @NotBlank(message = "Email can not be empty")
    @Email(message = "Invalid email!")
<<<<<<< HEAD
    private String email;

    @Enumerated(EnumType.STRING)
=======
    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
    private Role role;
}

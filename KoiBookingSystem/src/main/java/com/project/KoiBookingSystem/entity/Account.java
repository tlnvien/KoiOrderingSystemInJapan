package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.KoiBookingSystem.enums.Gender;
import com.project.KoiBookingSystem.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class Account implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Tên đăng nhập không thể để trống!")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Mật khẩu không thể để trống!")
    @Size(min = 6, message = "Mật khẩu phải chứa ít nhất 6 ký tự!")
    @Column(nullable = false)
    private String password;

    @Pattern(regexp = "(84[35789]|0[35789])\\d{8}", message = "Định dạng số điện thoại không hợp lệ!")
    @Column(unique = true)
    private String phone;

    @NotBlank(message = "Email không thể để trống!")
    @Email(message = "Định dạng email không hợp lệ!")
    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private LocalDate createdDate;

    @Pattern(regexp = "^CU\\d+$|^SS\\d+$|^CS\\d+$|^DS\\d+$|^MG\\d+$|^AD\\d+$|^FH\\d+$", message = "Invalid user ID")
    @Column(unique = true, nullable = false)
    private String userId;

    @Pattern(regexp = "^[\\p{L} ]+$", message = "Tên đầy đủ không được chứa số hoặc ký tự đặc biệt!")
    private String fullName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Past(message = "Ngày tháng năm sinh không hợp lệ!")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dob;

    private String address;

    private String note;

    @Column(nullable = false)
    private boolean status;

    @Column(nullable = false)
    private boolean confirmed;

    @Column(nullable = false)
    private double balance;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDateTime deleteRequestDate;

    @Column(nullable = false)
    private boolean pendingDeletion;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<AuthenticationCode> codes;

    @OneToOne(mappedBy = "farmHost", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Farm farm;

    @OneToMany(mappedBy = "consulting", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Tour> consulting;

    @OneToMany(mappedBy = "sales", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Tour> sales;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Booking> bookingCustomer;

    @OneToMany(mappedBy = "sales", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Booking> bookingSales;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Orders> orders;

    @OneToMany(mappedBy = "deliveringStaff", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Delivering> delivering;

    @OneToMany(mappedBy = "fromAccount", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Transactions> transactionsFrom;

    @OneToMany(mappedBy = "toAccount", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Transactions> transactionsTo;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Feedback> feedbackCustomer;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(this.role.toString()));
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.status;
    }

}

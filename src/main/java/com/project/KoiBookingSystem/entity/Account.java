package com.project.KoiBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.KoiBookingSystem.enums.Gender;
import com.project.KoiBookingSystem.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@Entity
public class Account implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    private float balance = 0;

    @NotBlank(message = "Username can not be empty!")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Password can not be empty!")
    @Size(min = 6, message = "Password must be at least 6 characters!")
    @Column(nullable = false)
    private String password;

    @Pattern(regexp = "(84|0[3|5|7|8|9])\\d{8}", message = "Invalid phone number")
    @Column(unique = true)
    private String phone;

    @NotBlank(message = "Email can not be empty")
    @Email(message = "Invalid email!")
    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private LocalDate createdDate;

    @Pattern(regexp = "^CU\\d+$|^SS\\d+$|^CS\\d+$|^DS\\d+$|^MG\\d+$|^AD\\d+$", message = "Invalid user ID")
    @Column(unique = true, nullable = false)
    private String userID;

    @Pattern(regexp = "^[a-zA-Z ]+$", message = "First name can not contain number")
    private String firstName;

    @Pattern(regexp = "^[a-zA-Z ]+$", message = "Last name can not contain number")
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Past(message = "Invalid date of birth")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dob;

    private String address;

    private String note;

    @Column(nullable = false)
    private boolean status;

    @Override
    // Định nghĩa những quyền hạn mà account có thể làm được
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(this.role.toString()));
        return authorities;
    }

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private  Set<Booking> bookings;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private Set<Order> orders;


    @OneToMany(mappedBy = "from")
    private Set<Transactions> transactionsFrom;

    @OneToMany(mappedBy = "to")
    private Set<Transactions> transactionsTo;


    @OneToMany (mappedBy = "account")
    private Set<Koi> koies;

    @OneToMany(mappedBy = "consulting", cascade = CascadeType.ALL)
    private Set<Booking> bookingsId;

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
        return true;
    }

}

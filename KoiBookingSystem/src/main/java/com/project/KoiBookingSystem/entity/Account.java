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

    @NotBlank(message = "Username can not be empty!")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Password can not be empty!")
    @Size(min = 6, message = "Password must be at least 6 characters!")
    @Column(nullable = false)
    private String password;

    @Pattern(regexp = "(84[35789]|0[35789])\\d{8}", message = "Invalid phone number")
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

    @Pattern(regexp = "^CU\\d+$|^SS\\d+$|^CS\\d+$|^DS\\d+$|^MG\\d+$|^AD\\d+$|^FH\\d+$", message = "Invalid user ID")
    @Column(unique = true, nullable = false)
    private String userId;

    @Pattern(regexp = "^[a-zA-Z ]+$", message = "First name can not contain number")
    private String fullName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Past(message = "Invalid date of birth")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dob;

    private String address;

    private String note;

    @Column(nullable = false)
    private boolean status;

    @Column(nullable = false)
    private boolean confirmed;

    private double balance;

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

    @OneToMany(mappedBy = "fromAccount", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Transactions> transactionsFrom;

    @OneToMany(mappedBy = "toAccount", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Transactions> transactionsTo;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Feedback> feedbackCustomer;

    @OneToMany(mappedBy = "consulting", cascade = CascadeType.ALL)
    private Set<Booking> bookingsCheck;//

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

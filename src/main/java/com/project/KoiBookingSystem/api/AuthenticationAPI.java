package com.project.KoiBookingSystem.api;

import com.project.KoiBookingSystem.model.request.LoginRequest;
import com.project.KoiBookingSystem.model.request.RegisterRequest;
import com.project.KoiBookingSystem.model.response.LoginResponse;
import com.project.KoiBookingSystem.model.response.RegisterResponse;
import com.project.KoiBookingSystem.service.AuthenticationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class AuthenticationAPI {

    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity registerAccount(@Valid @RequestBody RegisterRequest registerRequest) {
        RegisterResponse newAccount = authenticationService.registerAccount(registerRequest);
        return ResponseEntity.ok(newAccount);
    }

    @PostMapping("/register/staff")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity registerAccountByManager(@Valid @RequestBody RegisterRequest registerRequest) {
        RegisterResponse newAccount = authenticationService.registerAccountByManager(registerRequest);
        return ResponseEntity.ok(newAccount);
    }

    @PostMapping("/register/manager")
    public ResponseEntity registerManagerAccount(@Valid @RequestBody RegisterRequest registerRequest) {
        RegisterResponse newAccount = authenticationService.registerManagerAccount(registerRequest);
        return ResponseEntity.ok(newAccount);
    }

    @PostMapping("/login")
    public ResponseEntity loginAccount(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse thisAccount = authenticationService.loginAccount(loginRequest);
        return ResponseEntity.ok(thisAccount);
    }
}

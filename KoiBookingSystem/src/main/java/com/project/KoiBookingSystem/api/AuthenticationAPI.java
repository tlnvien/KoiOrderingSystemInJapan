package com.project.KoiBookingSystem.api;

import com.project.KoiBookingSystem.enums.Role;
import com.project.KoiBookingSystem.model.request.*;
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
    public ResponseEntity registerStaffAccount(@Valid @RequestBody RegisterRequest registerRequest, @RequestParam Role role) {
        RegisterResponse newAccount = authenticationService.registerStaffAccount(registerRequest, role);
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

    @PostMapping("/auth/google")
    public ResponseEntity loginGoogleAccount(@RequestBody TokenRequest tokenRequest) {
        String email = authenticationService.authenticationFirebase(tokenRequest.getToken());
        return ResponseEntity.ok(email);
    }

    @PostMapping("/auth/facebook")
    public ResponseEntity loginFacebookAccount(@RequestBody TokenRequest tokenRequest) {
        String email = authenticationService.authenticationFirebase(tokenRequest.getToken());
        return ResponseEntity.ok(email);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity forgotAccountPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        authenticationService.forgotAccountPassword(forgotPasswordRequest);
        return ResponseEntity.ok("Forgot Password Request Sent!");
    }

    @PostMapping("/reset-password")
    public ResponseEntity resetAccountPassword(@Valid @RequestBody ResetPasswordRequest request, @RequestParam String requestCode, @RequestParam String email) {
        authenticationService.resetAccountPassword(request, requestCode, email);
        return ResponseEntity.ok("Password Reset Successfully!");
    }

    @PostMapping("/register/confirm/{email}")
    public ResponseEntity confirmRegistration(@RequestParam String code, @PathVariable String email) {
        authenticationService.completeRegistration(code, email);
        return ResponseEntity.ok("Account confirmed successfully!");
    }
}

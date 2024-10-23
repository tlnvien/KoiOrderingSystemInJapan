package com.project.KoiBookingSystem.api;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.enums.Role;
import com.project.KoiBookingSystem.model.request.UserRequest;
import com.project.KoiBookingSystem.model.response.UserResponse;
import com.project.KoiBookingSystem.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/info")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class UserAPI {

    @Autowired
    UserService userService;

    @PutMapping
    public ResponseEntity updateInformation(@Valid @RequestBody UserRequest account) {
        UserResponse updatedAccount = userService.updateInformation(account);
        return ResponseEntity.ok(updatedAccount);
    }

    @DeleteMapping
    public ResponseEntity deleteUser() {
        UserResponse deletedAccount = userService.deleteUser();
        return ResponseEntity.ok(deletedAccount);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/role/{role}")
    public ResponseEntity getUsersByRole(@PathVariable Role role) {
        List<UserResponse> userResponses = userService.getAllUsersByRole(role);
        return ResponseEntity.ok(userResponses);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity getUserInfoByUserId(@PathVariable String userId) {
        UserResponse userResponse = userService.getAccountByUserId(userId);
        return ResponseEntity.ok(userResponse);
    }
}

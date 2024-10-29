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

    @DeleteMapping("{userId}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity deleteUser(@PathVariable String userId) {
        UserResponse deletedAccount = userService.deleteUser(userId);
        return ResponseEntity.ok(deletedAccount);
    }

    @PutMapping("{userId}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity unlockUser(@PathVariable String userId) {
        UserResponse unlockedAccount = userService.unlockUser(userId);
        return ResponseEntity.ok(unlockedAccount);
    }

    @DeleteMapping("/request")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity deleteAccountRequest() {
        userService.deleteAccountRequest();
        return ResponseEntity.ok("Yêu cầu xóa tài khoản của bạn đã được gửi, tài khoản của bạn sẽ bị xóa trong 3 ngày. Để hủy quá trình, vui lòng đăng nhập lại bằng tài khoản này!");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/user/role")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity getUsersByRole(@RequestParam Role role) {
        List<UserResponse> userResponses = userService.getAllUsersByRole(role);
        return ResponseEntity.ok(userResponses);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyAuthority('MANAGER', 'SALES', 'CONSULTING', 'DELIVERING')")
    public ResponseEntity getUserInfoByUserId(@PathVariable String userId) {
        UserResponse userResponse = userService.getAccountByUserId(userId);
        return ResponseEntity.ok(userResponse);
    }
}

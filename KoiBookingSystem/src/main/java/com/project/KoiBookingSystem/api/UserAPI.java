package com.project.KoiBookingSystem.api;

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

    // Lấy ra tất cả thông tin của tụi User (chỉ có Manager mới có thể làm được)
    @GetMapping
    @PreAuthorize("hasAuthority('MANAGER')") // Trước khi chạy hàm này sẽ Authorize cái quyền của những account có role là Manager
    public ResponseEntity getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{role}")
    public ResponseEntity getUsersByRole(@PathVariable String role) {
        List<UserResponse> userResponses = userService.getAllUsersByRole(role);
        return ResponseEntity.ok(userResponses);
    }
}

package com.project.KoiBookingSystem.api;

import com.project.KoiBookingSystem.model.request.FarmHostRequest;
import com.project.KoiBookingSystem.model.response.FarmHostResponse;
import com.project.KoiBookingSystem.service.RequestService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/request")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class RequestAPI {

    @Autowired
    RequestService requestService;


    @PostMapping
    @PreAuthorize("hasAuthority('FARM_HOST')")
    public ResponseEntity createNewRequest(@RequestBody @Valid FarmHostRequest farmHostRequest) {
        FarmHostResponse farmHostResponse = requestService.createNewRequest(farmHostRequest);
        return ResponseEntity.ok(farmHostResponse);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity getAllFarmHostRequest() {
        List<FarmHostResponse> farmHostResponses = requestService.getAllFarmHostRequest();
        return ResponseEntity.ok(farmHostResponses);
    }


    @GetMapping("/notDone")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity getAllRequestNotDone() {
        List<FarmHostResponse> farmHostResponses = requestService.getAllRequestNotDone();
        return ResponseEntity.ok(farmHostResponses);
    }

    @GetMapping("/done")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity getAllRequestDone() {
        List<FarmHostResponse> farmHostResponses = requestService.getAllRequestDone();
        return ResponseEntity.ok(farmHostResponses);
    }

    @GetMapping("/{farmHostId}")
    @PreAuthorize("hasAnyAuthority('MANAGER', 'FARM_HOST')")
    public ResponseEntity getAllRequestByFarmHost(@PathVariable String farmHostId) {
        List<FarmHostResponse> farmHostResponses = requestService.getAllRequestByFarmHost(farmHostId);
        return ResponseEntity.ok(farmHostResponses);
    }

    @PutMapping("/completed/{requestId}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity markCompletedRequest(@PathVariable String requestId) {
        FarmHostResponse farmHostResponse = requestService.markCompletedRequest(requestId);
        return ResponseEntity.ok(farmHostResponse);
    }

}

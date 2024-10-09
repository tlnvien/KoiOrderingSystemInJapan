package com.project.KoiBookingSystem.api;


import com.project.KoiBookingSystem.enums.RequestStatus;
import com.project.KoiBookingSystem.model.request.RequestRequest;
import com.project.KoiBookingSystem.model.response.RequestResponse;
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
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity createRequest(@Valid @RequestBody RequestRequest requestRequest) {
        RequestResponse requestResponse = requestService.createRequest(requestRequest);
        return ResponseEntity.ok(requestResponse);
    }

    @GetMapping("/search")
    public ResponseEntity searchRequests(@RequestParam(required = false) String requestId, @RequestParam(required = false) String customerId, @RequestParam(required = false) RequestStatus status) {
        List<RequestResponse> requestResponses = requestService.searchRequests(requestId, customerId, status);
        return ResponseEntity.ok(requestResponses);
    }

    @GetMapping
    public ResponseEntity getAllRequests() {
        List<RequestResponse> requestResponses = requestService.getAllRequests();
        return ResponseEntity.ok(requestResponses);
    }

    @PostMapping("/take/{requestId}")
    @PreAuthorize("hasAuthority('SALES')")
    public ResponseEntity takeRequest(@PathVariable String requestId) {
        requestService.takeRequest(requestId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/complete/{requestId}")
    @PreAuthorize("hasAuthority('SALES')")
    public ResponseEntity completeRequest(@PathVariable String requestId) {
        requestService.completeRequest(requestId);
        return ResponseEntity.noContent().build();
    }

}

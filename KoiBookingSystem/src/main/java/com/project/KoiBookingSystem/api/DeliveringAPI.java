package com.project.KoiBookingSystem.api;

import com.project.KoiBookingSystem.enums.DeliveringStatus;
import com.project.KoiBookingSystem.model.request.DeliveringRequest;
import com.project.KoiBookingSystem.model.response.DeliveringResponse;
import com.project.KoiBookingSystem.service.DeliveringService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/delivering")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class DeliveringAPI {

    @Autowired
    DeliveringService deliveringService;

    @PostMapping
    public ResponseEntity createNewDelivering(@RequestBody DeliveringRequest deliveringRequest) {
        DeliveringResponse deliveringResponse = deliveringService.createNewDelivering(deliveringRequest);
        return ResponseEntity.ok(deliveringResponse);
    }

    @PostMapping("/order/{deliveringId}")
    public ResponseEntity addOrderToDelivering(@PathVariable String deliveringId, @RequestParam String orderId) {
        DeliveringResponse deliveringResponse = deliveringService.addOrderToDelivering(deliveringId, orderId);
        return ResponseEntity.ok(deliveringResponse);
    }

    @DeleteMapping("/order/{deliveringId}")
    public ResponseEntity removeOrderFromDelivering(String deliveringId, String orderId) {
        DeliveringResponse deliveringResponse = deliveringService.removeOrderFromDelivering(deliveringId, orderId);
        return ResponseEntity.ok(deliveringResponse);
    }

    @GetMapping("/all")
    public ResponseEntity getAllDelivering() {
        List<DeliveringResponse> deliveringResponses = deliveringService.getAllDelivering();
        return ResponseEntity.ok(deliveringResponses);
    }


    @GetMapping("/all/status")
    public ResponseEntity getAllDeliveringByStatus(@RequestParam DeliveringStatus status) {
        List<DeliveringResponse> deliveringResponses = deliveringService.getAllDeliveringByStatus(status);
        return ResponseEntity.ok(deliveringResponses);
    }

    @PutMapping("/start/{deliveringId}")
    public ResponseEntity startDelivering(@PathVariable String deliveringId) {
        DeliveringResponse deliveringResponse = deliveringService.startDelivering(deliveringId);
        return ResponseEntity.ok(deliveringResponse);
    }

    @PutMapping("/end/{deliveringId}")
    public ResponseEntity endDelivering(@PathVariable String deliveringId) {
        DeliveringResponse deliveringResponse = deliveringService.endDelivering(deliveringId);
        return ResponseEntity.ok(deliveringResponse);
    }
}

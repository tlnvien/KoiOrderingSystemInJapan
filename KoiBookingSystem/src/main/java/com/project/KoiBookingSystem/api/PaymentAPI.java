package com.project.KoiBookingSystem.api;

import com.project.KoiBookingSystem.entity.Payment;
import com.project.KoiBookingSystem.enums.PaymentStatus;
import com.project.KoiBookingSystem.enums.PaymentType;
import com.project.KoiBookingSystem.model.response.PaymentResponse;
import com.project.KoiBookingSystem.service.PaymentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class PaymentAPI {

    @Autowired
    PaymentService paymentService;

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('SALES', 'CONSULTING', 'DELIVERING', 'MANAGER')")
    public ResponseEntity searchPayments(@RequestParam(required = false) String paymentId, @RequestParam(required = false) String customerId, @RequestParam(required = false)PaymentStatus status) {
        List<PaymentResponse> paymentResponses = paymentService.searchPayments(paymentId, customerId, status);
        return ResponseEntity.ok(paymentResponses);
    }

    @PostMapping("/initiate")
//    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity initiatePayment(@RequestParam String id, @RequestParam String method, @RequestParam double price, @RequestParam String description, @RequestParam PaymentType paymentType) {
        String paymentUrl = paymentService.initiatePayment(id, method, price, description, paymentType);
        return ResponseEntity.ok(paymentUrl);
    }

    @PostMapping("/complete/{paymentId}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity completePayment(@PathVariable String paymentId, @RequestParam Map<String, String> queryParams) {
        paymentService.completePayment(paymentId, queryParams);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refund/{paymentId}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity refundPayment(@PathVariable String paymentId) {
        paymentService.refundPayment(paymentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{paymentId}")
    @PreAuthorize("hasAnyAuthority('SALES', 'CONSULTING', 'DELIVERING', 'MANAGER')")
    public ResponseEntity getPaymentDetails(@PathVariable String paymentId) {
        PaymentResponse payment = paymentService.getPaymentDetails(paymentId);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/cancel/{paymentId}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity cancelPayment(@PathVariable String paymentId) {
        paymentService.cancelPayment(paymentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('SALES', 'CONSULTING', 'DELIVERING', 'MANAGER')")
    public ResponseEntity getAllPayments() {
        List<PaymentResponse> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/search/customer")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity searchPaymentByCustomerRole(@RequestParam String paymentId, @RequestParam PaymentStatus status) {
        List<PaymentResponse> payments = paymentService.searchPaymentByCustomerRole(paymentId, status);
        return ResponseEntity.ok(payments);
    }
}

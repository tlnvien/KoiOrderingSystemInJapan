package com.project.KoiBookingSystem.api;

import com.project.KoiBookingSystem.entity.Payment;
import com.project.KoiBookingSystem.enums.OrderPaymentStatus;
import com.project.KoiBookingSystem.enums.OrderStatus;
import com.project.KoiBookingSystem.enums.PaymentCurrency;
import com.project.KoiBookingSystem.model.request.DeliveredDateRequest;
import com.project.KoiBookingSystem.model.request.OrderRequest;
import com.project.KoiBookingSystem.model.response.OrderResponse;
import com.project.KoiBookingSystem.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class OrderAPI {

    @Autowired
    OrderService orderService;


    @PostMapping("{customerId}")
    @PreAuthorize("hasAuthority('CONSULTING')")
    public ResponseEntity createNewOrder(@Valid @RequestBody OrderRequest orderRequest, @RequestParam String tourId, @PathVariable String customerId) {
        OrderResponse orderResponse = orderService.createNewOrder(orderRequest, tourId, customerId);
        return ResponseEntity.ok(orderResponse);
    }

    @GetMapping("customer/{customerId}")
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'CONSULTING', 'MANAGER')")
    public ResponseEntity getAllOrdersByCustomer(@PathVariable String customerId) {
        List<OrderResponse> orderResponses = orderService.getAllOrdersByCustomer(customerId);
        return ResponseEntity.ok(orderResponses);
    }

    @GetMapping("/tour/{tourId}")
    @PreAuthorize("hasAnyAuthority('CONSULTING', 'MANAGER', 'DELIVERING')")
    public ResponseEntity getAllOrdersByTour(@PathVariable String tourId) {
        List<OrderResponse> orderResponses = orderService.getAllOrdersByTour(tourId);
        return ResponseEntity.ok(orderResponses);
    }


    @GetMapping("/list/received")
    @PreAuthorize("hasAnyAuthority('CONSULTING', 'MANAGER', 'DELIVERING')")
    public ResponseEntity getAllOrdersReceived() {
        List<OrderResponse> orderResponses = orderService.getAllOrderReceived();
        return ResponseEntity.ok(orderResponses);
    }

    @GetMapping("/farmHost/{farmId}")
    @PreAuthorize("hasAuthority('FARM_HOST')")
    public ResponseEntity getAllOrdersByFarmHost(String farmId) {
        List<OrderResponse> orderResponses = orderService.getAllOrdersByFarmHost(farmId);
        return ResponseEntity.ok(orderResponses);
    }

    @GetMapping("/farm/{farmId}")
    @PreAuthorize("hasAnyAuthority('CONSULTING', 'DELIVERING', 'MANAGER')")
    public ResponseEntity getAllOrdersByFarm(String farmId) {
        List<OrderResponse> orderResponses = orderService.getAllOrdersByFarm(farmId);
        return ResponseEntity.ok(orderResponses);
    }

    @PostMapping("/farmHost/{orderId}")
    @PreAuthorize("hasAuthority('FARM_HOST')")
    public ResponseEntity updateOrderStatusByFarmHost(@PathVariable String orderId, @RequestParam OrderStatus status) {
        OrderResponse orderResponse = orderService.updateOrderStatusByFarmHost(orderId, status);
        return ResponseEntity.ok(orderResponse);
    }

    @PostMapping("/consulting/{orderId}")
    @PreAuthorize("hasAuthority('CONSULTING')")
    public ResponseEntity updateOrderStatusByConsulting(@PathVariable String orderId, @RequestParam OrderStatus status) {
        OrderResponse orderResponse = orderService.updateOrderStatusByConsulting(orderId, status);
        return ResponseEntity.ok(orderResponse);
    }


    @PostMapping("/delivering/{orderId}")
    @PreAuthorize("hasAuthority('DELIVERING')")
    public ResponseEntity updateOrderStatusByDelivering(@PathVariable String orderId, @RequestParam OrderStatus status) {
        OrderResponse orderResponse = orderService.updateOrderStatusByDelivering(orderId, status);
        return ResponseEntity.ok(orderResponse);
    }

    @PostMapping("/paymentUrl/{orderId}")
    public ResponseEntity createOrderPaymentUrl(@PathVariable String orderId, @RequestParam OrderPaymentStatus isFinalPayment) {
        String paymentUrl = orderService.createOrderPaymentUrl(orderId, isFinalPayment);
        return ResponseEntity.ok(paymentUrl);
    }

    @PostMapping("/transactions/first/{orderId}")
    public ResponseEntity createFirstOrderTransaction(@PathVariable String orderId) {
        orderService.createFirstOrderTransaction(orderId);
        return ResponseEntity.ok("Thanh toán thành công!");
    }

    @PostMapping("/transactions/final/{orderId}")
    public ResponseEntity createFinalOrderTransaction(@PathVariable String orderId) {
        orderService.createFinalOrderTransaction(orderId);
        return ResponseEntity.ok("Thanh toán thành công!");
    }

    @PostMapping("/cash/first/{orderId}")
    public ResponseEntity handleCashFirstPayment(@PathVariable String orderId, @RequestParam PaymentCurrency currency) {
        orderService.handleCashFirstPayment(orderId, currency);
        return ResponseEntity.ok("Thanh toán thành công!");
    }

    @PostMapping("/cash/final/{orderId}")
    public ResponseEntity handleCashFinalPayment(@PathVariable String orderId, @RequestParam PaymentCurrency currency) {
        orderService.handleCashFinalPayment(orderId, currency);
        return ResponseEntity.ok("Thanh toán thành công!");
    }


    @PutMapping("/expected/{orderId}")
    public ResponseEntity updateExpectedDeliveredDate(@PathVariable String orderId, @Valid @RequestBody DeliveredDateRequest deliveredDateRequest) {
        OrderResponse orderResponse = orderService.updateExpectedDeliveredDate(orderId, deliveredDateRequest);
        return ResponseEntity.ok(orderResponse);
    }



}

package com.project.KoiBookingSystem.api;

import com.project.KoiBookingSystem.entity.Payment;
import com.project.KoiBookingSystem.enums.OrderStatus;
import com.project.KoiBookingSystem.model.request.OrderRequest;
import com.project.KoiBookingSystem.model.response.OrderResponse;
import com.project.KoiBookingSystem.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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


    @PostMapping
    public ResponseEntity createNewOrder(@Valid @RequestBody OrderRequest orderRequest, @RequestParam String tourId, @PathVariable String customerId) {
        OrderResponse orderResponse = orderService.createNewOrder(orderRequest, tourId, customerId);
        return ResponseEntity.ok(orderResponse);
    }

    @GetMapping("{customerId}")
    public ResponseEntity getAllOrdersByCustomer(@PathVariable String customerId) {
        List<OrderResponse> orderResponses = orderService.getAllOrdersByCustomer(customerId);
        return ResponseEntity.ok(orderResponses);
    }

    @GetMapping("{tourId}")
    public ResponseEntity getAllOrdersByTour(@PathVariable String tourId) {
        List<OrderResponse> orderResponses = orderService.getAllOrdersByTour(tourId);
        return ResponseEntity.ok(orderResponses);
    }


    @PostMapping("/farmHost/{orderId}")
    public ResponseEntity updateOrderStatusByFarmHost(@PathVariable String orderId, @RequestParam OrderStatus status) {
        OrderResponse orderResponse = orderService.updateOrderStatusByFarmHost(orderId, status);
        return ResponseEntity.ok(orderResponse);
    }


    @PostMapping("/consulting/{orderId}")
    public ResponseEntity updateOrderStatusByConsulting(@PathVariable String orderId, @RequestParam OrderStatus status) {
        OrderResponse orderResponse = orderService.updateOrderStatusByConsulting(orderId, status);
        return ResponseEntity.ok(orderResponse);
    }


    @PostMapping("/delivering/{orderId}")
    public ResponseEntity updateOrderStatusByDelivering(@PathVariable String orderId, @RequestParam OrderStatus status) {
        OrderResponse orderResponse = orderService.updateOrderStatusByDelivering(orderId, status);
        return ResponseEntity.ok(orderResponse);
    }

    @PostMapping("/paymentUrl/{orderId}")
    public ResponseEntity createOrderPaymentUrl(@PathVariable String orderId) {
        String paymentUrl = orderService.createOrderPaymentUrl(orderId);
        return ResponseEntity.ok(paymentUrl);
    }

    @PostMapping("/transactions/{orderId}")
    public ResponseEntity createOrderTransaction(@PathVariable String orderId) {
        orderService.createOrderTransaction(orderId);
        return ResponseEntity.ok("Payment successfully!");
    }
}

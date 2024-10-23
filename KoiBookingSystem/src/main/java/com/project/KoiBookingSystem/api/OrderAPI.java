package com.project.KoiBookingSystem.api;

import com.project.KoiBookingSystem.entity.Payment;
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
    public ResponseEntity createNewOrder(@Valid @RequestBody OrderRequest orderRequest, @RequestParam String tourId, @PathVariable String customerId) {
        OrderResponse orderResponse = orderService.createNewOrder(orderRequest, tourId, customerId);
        return ResponseEntity.ok(orderResponse);
    }

    @GetMapping("customer/{customerId}")
    public ResponseEntity getAllOrdersByCustomer(@PathVariable String customerId) {
        List<OrderResponse> orderResponses = orderService.getAllOrdersByCustomer(customerId);
        return ResponseEntity.ok(orderResponses);
    }

    @GetMapping("/tour/{tourId}")
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
    public ResponseEntity createOrderPaymentUrl(@PathVariable String orderId, @RequestParam(value = "isFinalPayment") boolean isFinalPayment) {
        String paymentUrl = orderService.createOrderPaymentUrl(orderId, isFinalPayment);
        return ResponseEntity.ok(paymentUrl);
    }

    @PostMapping("/transactions/first/{orderId}")
    public ResponseEntity createFirstOrderTransaction(@PathVariable String orderId) {
        orderService.createFirstOrderTransaction(orderId);
        return ResponseEntity.ok("Payment successfully!");
    }

    @PostMapping("/transactions/final/{orderId}")
    public ResponseEntity createFinalOrderTransaction(@PathVariable String orderId) {
        orderService.createFinalOrderTransaction(orderId);
        return ResponseEntity.ok("Payment successfully!");
    }

    @PostMapping("/cash/first/{orderId}")
    public ResponseEntity handleCashFirstPayment(@PathVariable String orderId, @RequestParam PaymentCurrency currency) {
        orderService.handleCashFirstPayment(orderId, currency);
        return ResponseEntity.ok("Payment successfully!");
    }

    @PostMapping("/cash/final/{orderId}")
    public ResponseEntity handleCashFinalPayment(@PathVariable String orderId, @RequestParam PaymentCurrency currency) {
        orderService.handleCashFinalPayment(orderId, currency);
        return ResponseEntity.ok("Payment successfully!");
    }


    @PutMapping("/expected/{orderId}")
    public ResponseEntity updateExpectedDeliveredDate(@PathVariable String orderId, @Valid @RequestBody DeliveredDateRequest deliveredDateRequest) {
        OrderResponse orderResponse = orderService.updateExpectedDeliveredDate(orderId, deliveredDateRequest);
        return ResponseEntity.ok(orderResponse);
    }

//    @PutMapping("/confirm/{deliveringId}")
//    public ResponseEntity confirmOrderDelivered(@PathVariable String deliveringId, @RequestParam String orderId, @RequestParam OrderStatus status) {
//        OrderResponse orderResponse = orderService.confirmOrderDelivered(deliveringId, orderId, status);
//        return ResponseEntity.ok(orderResponse);
//    }
}

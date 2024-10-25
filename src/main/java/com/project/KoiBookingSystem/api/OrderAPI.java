package com.project.KoiBookingSystem.api;

import com.project.KoiBookingSystem.entity.Orders;
import com.project.KoiBookingSystem.model.request.OrderRequest;
import com.project.KoiBookingSystem.model.response.OrderResponse;
import com.project.KoiBookingSystem.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/order")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class OrderAPI {

    @Autowired
    OrderService orderService;

    @PostMapping("/cash")
    public ResponseEntity createOrder(@RequestBody @Valid OrderRequest orderRequest) {
        OrderResponse orderResponse = orderService.createOrder(orderRequest);
        return ResponseEntity.ok(orderResponse);
    }

    @PostMapping("/vnpay")
    public ResponseEntity createNewOrder(@Valid @RequestBody OrderRequest orderRequest) throws Exception {
        String vnPayURL = orderService.createUrl(orderRequest);
        return ResponseEntity.ok(vnPayURL);
    }

    @GetMapping  //lấy all Order không cần lọc
    public ResponseEntity getAllOrders() {
        List<Orders> orders = orderService.getAllOrder();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/customer")  //lấy all Order của Customer (đang login)
    public ResponseEntity getAllOrderOfCustomer() {
        List<Orders> orders = orderService.getAllOrderOfCustomer();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{customerId}")  //lấy all Order của Customer cụ thể thông qua customerId
    public ResponseEntity getAllIndividualOrder(@PathVariable String customerId) {
        List<Orders> orders = orderService.getAllIndividualOrder(customerId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/tour/{tourId}")
    public ResponseEntity getAllOrdersByTour(@PathVariable String tourId) {
        List<OrderResponse> orderResponses = orderService.getAllOrdersByTour(tourId);
        return ResponseEntity.ok(orderResponses);
    }

    @PostMapping("transaction")
    public ResponseEntity createNewOrder(@RequestParam UUID orderID) {
         orderService.createTransaction(orderID);
        return ResponseEntity.ok("Success");
    }
}

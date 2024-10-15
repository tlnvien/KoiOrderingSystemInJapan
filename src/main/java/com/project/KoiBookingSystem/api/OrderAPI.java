package com.project.KoiBookingSystem.api;

import com.project.KoiBookingSystem.entity.Orders;
import com.project.KoiBookingSystem.model.request.OrderRequest;
import com.project.KoiBookingSystem.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    @PostMapping
    public ResponseEntity createNewOrder(@RequestBody OrderRequest orderRequest) throws Exception {
        String vnPayURL = orderService.createUrl(orderRequest);
        return ResponseEntity.ok(vnPayURL);
    }

    @GetMapping
    public ResponseEntity getAllOrders() {
        List<Orders> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @PostMapping("transaction")
    public ResponseEntity createNewOrder(@RequestParam UUID orderID) {
         orderService.createTransaction(orderID);
        return ResponseEntity.ok("Success");
    }
}

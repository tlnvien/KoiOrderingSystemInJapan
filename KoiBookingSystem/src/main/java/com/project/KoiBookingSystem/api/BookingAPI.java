package com.project.KoiBookingSystem.api;

import com.project.KoiBookingSystem.enums.BookingStatus;
import com.project.KoiBookingSystem.enums.TourType;
import com.project.KoiBookingSystem.model.request.BookingRequest;
import com.project.KoiBookingSystem.model.response.BookingResponse;
import com.project.KoiBookingSystem.service.BookingService;
import com.project.KoiBookingSystem.service.VNPayService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/booking")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class BookingAPI {

    @Autowired
    BookingService bookingService;

    @Autowired
    VNPayService vnPayService;

    @PostMapping("/paymentUrl/{bookingId}")
    public ResponseEntity createBookingPaymentUrl(@PathVariable String bookingId) {
        String vnPayURL = bookingService.createBookingPaymentUrl(bookingId);
        return ResponseEntity.ok(vnPayURL);
    }

    @PostMapping("/transaction/{bookingId}")
    public ResponseEntity createTransaction(@PathVariable String bookingId) {
        bookingService.createBookingTransaction(bookingId);
        return ResponseEntity.ok("Thanh toán thành công!");
    }


    @PostMapping("/request")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity createNewRequestedBooking(@Valid @RequestBody BookingRequest bookingRequest) {
        BookingResponse bookingResponse = bookingService.createNewRequestedBooking(bookingRequest);
        return ResponseEntity.ok(bookingResponse);
    }

    @GetMapping("/consulting")
    @PreAuthorize("hasAuthority('CONSULTING')")
    public ResponseEntity getBookingInfoByConsulting() {
        List<BookingResponse> bookingResponses = bookingService.getAllBookingInfoByConsulting();
        return ResponseEntity.ok(bookingResponses);
    }

    @GetMapping("/requests")
    @PreAuthorize("hasAuthority('SALES')")
    public ResponseEntity getAllRequests() {
        List<BookingResponse> requests = bookingService.getAllRequests();
        return ResponseEntity.ok(requests);
    }

    @GetMapping("{bookingId}")
    public ResponseEntity getBookingById(@PathVariable String bookingId) {
        BookingResponse bookingResponse = bookingService.getBookingById(bookingId);
        return ResponseEntity.ok(bookingResponse);
    }

    @PostMapping("/check/{bookingId}")
    @PreAuthorize("hasAuthority('CONSULTING')")
    public ResponseEntity checkInBooking(@PathVariable String bookingId, @RequestParam BookingStatus status) {
        BookingResponse bookingResponse = bookingService.checkInBooking(bookingId, status);
        return ResponseEntity.ok(bookingResponse);
    }

    @PostMapping("/take/{bookingId}")
    @PreAuthorize("hasAuthority('SALES')")
    public ResponseEntity takeRequestBooking(@PathVariable String bookingId) {
        BookingResponse bookingResponse = bookingService.takeRequestBooking(bookingId);
        return ResponseEntity.ok(bookingResponse);
    }

    @PostMapping("/associate")
    @PreAuthorize("hasAuthority('SALES')")
    public ResponseEntity associateBookingToRequestedTour(@RequestParam String bookingId, @RequestParam String tourId) {
        BookingResponse bookingResponse = bookingService.associateBookingToRequestedTour(bookingId, tourId);
        return ResponseEntity.ok(bookingResponse);
    }
}

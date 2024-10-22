package com.project.KoiBookingSystem.api;

import com.project.KoiBookingSystem.model.request.BookingAvailableRequest;
import com.project.KoiBookingSystem.model.response.BookingAvailableResponse;
import com.project.KoiBookingSystem.service.BookingAvailableService;
import com.project.KoiBookingSystem.service.VNPayServiceAvailableTour;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/booking/available")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class BookingAvailableAPI {


    @Autowired
    BookingAvailableService bookingAvailableService;

    @Autowired
    VNPayServiceAvailableTour vnPayServiceAvailableTour;

    // tạo url
    @PostMapping("/ticketByBanking")
    public ResponseEntity createNewBooking(@Valid @RequestBody BookingAvailableRequest bookingRequest) throws Exception {
        String vnPayURL = vnPayServiceAvailableTour.createUrl(bookingRequest);
        return ResponseEntity.ok(vnPayURL);
    }

    // Xem luồng payment.
    @PostMapping("api/booking/available/transaction/{bookingID}")
    public ResponseEntity createNewOrder(@RequestParam String bookingID) {
        bookingAvailableService.createTransaction(bookingID);
        return ResponseEntity.ok("Payment Successful");
    }

    // tạo vé ko cần url ( để font-end) xem
    @PostMapping("createNoUrl")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity createTicket(@Valid @RequestBody BookingAvailableRequest bookingRequest) {
        BookingAvailableResponse ticket = bookingAvailableService.createTicket(bookingRequest);
        return ResponseEntity.ok(ticket);
    }

    // thanh toán bằng tiền mặt
    @PostMapping("createTicketCast")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity createTicketCast(@Valid @RequestBody BookingAvailableRequest bookingRequest) {
        BookingAvailableResponse ticket = bookingAvailableService.createTicketCast(bookingRequest);
        return ResponseEntity.ok(ticket);
    }


    // update check tại sân bay
    @PutMapping("/api/booking/checking/{bookingID}/{consultingID}")
    @PreAuthorize("hasAuthority('CONSULTING')")
    public ResponseEntity updateBooking(@PathVariable String bookingID, @PathVariable String consultingID) {
        BookingAvailableResponse updateStatus = bookingAvailableService.confirm(bookingID, consultingID);
        return ResponseEntity.ok(updateStatus);
    }

    //danh sách vé của khách hàng
    @PutMapping("/api/booking/listBooking/{customerID}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity getAllBookings(@PathVariable String customerID) {
        List<BookingAvailableResponse> bookings = bookingAvailableService.getAllBookings(customerID);
        return ResponseEntity.ok(bookings);
    }

    //danh sách vé của thằng consulting
    @GetMapping("/listBooking/consulting")
    @PreAuthorize("hasAuthority('CONSULTING')")
    public ResponseEntity getExpiredBookings(@RequestParam String tourId) {
        List<BookingAvailableResponse> listTicket = bookingAvailableService.getExpiredBookings(tourId);
        return ResponseEntity.ok(listTicket);
    }
}


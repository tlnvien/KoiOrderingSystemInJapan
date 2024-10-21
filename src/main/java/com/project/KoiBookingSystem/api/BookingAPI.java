package com.project.KoiBookingSystem.api;

import com.project.KoiBookingSystem.model.request.BookingRequest;
import com.project.KoiBookingSystem.model.response.BookingResponse;
import com.project.KoiBookingSystem.model.response.TourResponse;
import com.project.KoiBookingSystem.service.BookingService;
import com.project.KoiBookingSystem.service.TourService;
import com.project.KoiBookingSystem.service.VnPayServiceAvailableTour;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/booking")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class BookingAPI {

    @Autowired
    TourService tourService;

    @Autowired
    BookingService bookingService;

    @Autowired
    VnPayServiceAvailableTour vnPayServiceAvailableTour;

    // tạo url
    @PostMapping("/ticketByBanking")
    public ResponseEntity createNewBooking(@Valid @RequestBody BookingRequest bookingRequest) throws Exception {
        String vnPayURL = vnPayServiceAvailableTour.createUrl(bookingRequest);
        return ResponseEntity.ok(vnPayURL);
    }
    // Xem luồng payment
    @PostMapping("transaction/{bookingID}")
    public ResponseEntity createNewOrder(@RequestParam String bookingID) {
        bookingService.createTransaction(bookingID);
        return ResponseEntity.ok("Success");
    }
    // tạo vé ko cần url ( để font-end) xem
    @PostMapping("createNoUrl")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity createTicket(@Valid @RequestBody BookingRequest bookingRequest) {
        BookingResponse ticket = bookingService.createTicket(bookingRequest);
        return ResponseEntity.ok(ticket);
    }

    // thanh toán bằng tiền mặt
    @PostMapping("createTicketCast")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity createTicketCast(@Valid @RequestBody BookingRequest bookingRequest) {
        BookingResponse ticket = bookingService.createTicketCast(bookingRequest);
        return ResponseEntity.ok(ticket);
    }


    // hiển thị thông tin chi tiết của tour khi chọn của customer
    @GetMapping("/{tourID}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity getAllTourBooking(@PathVariable String tourID) {
        List<TourResponse> tours = tourService.getToursByTourID(tourID);
        return ResponseEntity.ok(tours);
    }

    // update check tại sân bay
    @PutMapping("/api/booking/checking/{bookingID}/{consultingID}")
    @PreAuthorize("hasAuthority('CONSULTING')")
    public ResponseEntity updateBooking(@PathVariable String bookingID, @PathVariable String consultingID) {
        BookingResponse updateStatus = bookingService.confirm(bookingID, consultingID);
        return ResponseEntity.ok(updateStatus);
    }

    // tìm tất cả booking của customer
    @PutMapping("/api/booking/listBooking/{customerID}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity getAllBookings(@PathVariable String customerID) {
        List<BookingResponse> bookings = bookingService.getAllBookings(customerID);
        return ResponseEntity.ok(bookings);
    }


//    // check thời gian thanh toán tại sân bay
//    @PutMapping("payment/{bookingID}/{userID}")
//    @PreAuthorize("hasAuthority('SALES')")
//    public ResponseEntity checkPaymentBooking(@PathVariable String bookingID, @PathVariable String userID) {
//        BookingResponse updateStatus = bookingService.confirmPaymentBooking(bookingID, userID);
//        return ResponseEntity.ok(updateStatus);
//    }
}
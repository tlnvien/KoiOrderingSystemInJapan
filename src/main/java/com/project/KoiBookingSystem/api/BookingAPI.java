package com.project.KoiBookingSystem.api;

import com.project.KoiBookingSystem.entity.Account;
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

    @PostMapping("createUrl")
    public ResponseEntity createNewBooking(@Valid @RequestBody BookingRequest bookingRequest) throws Exception {
        String vnPayURL = vnPayServiceAvailableTour.createUrl(bookingRequest);
        return ResponseEntity.ok(vnPayURL);
    }

    @PostMapping("transaction/{bookingId}")
    public ResponseEntity createNewOrder(@RequestParam String bookingID) {
        bookingService.createTransaction(bookingID);
        return ResponseEntity.ok("Success");
    }
    @PostMapping("createNoUrl")
    public ResponseEntity createTicket(@Valid @RequestBody BookingRequest bookingRequest) {
        BookingResponse ticket = bookingService.createTicket(bookingRequest);
        return ResponseEntity.ok(ticket);
    }


    // hiển thị thông tin chi tiết của tour khi chọn
    @GetMapping("/{tourID}")
    public ResponseEntity getAllTourBooking(@PathVariable String tourID) {
        List<TourResponse> tours = tourService.getToursByTourID(tourID);
        return ResponseEntity.ok(tours);
    }


    @PutMapping("{bookingID}/{userID}")
    public ResponseEntity updateBooking(@PathVariable String bookingID, @PathVariable String userID) {
        BookingResponse updateStatus = bookingService.confirm(bookingID, userID);
        return ResponseEntity.ok(updateStatus);
    }

    @PutMapping("{customerID}")
    public ResponseEntity getAllBookings(@PathVariable String customerID) {
        List<BookingResponse> bookings = bookingService.getAllBookings(customerID);
        return ResponseEntity.ok(bookings);
    }
}
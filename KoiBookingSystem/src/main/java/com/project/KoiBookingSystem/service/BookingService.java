package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.Booking;
import com.project.KoiBookingSystem.entity.Payment;
import com.project.KoiBookingSystem.entity.Request;
import com.project.KoiBookingSystem.entity.Tour;
import com.project.KoiBookingSystem.enums.BookingStatus;
import com.project.KoiBookingSystem.enums.TourType;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.model.response.BookingResponse;
import com.project.KoiBookingSystem.repository.BookingRepository;
import com.project.KoiBookingSystem.repository.PaymentRepository;
import com.project.KoiBookingSystem.repository.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BookingService {

    @Autowired
    TourRepository tourRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    BookingRepository bookingRepository;

    public BookingResponse createRequestTourBooking(String paymentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId);
        if (payment == null) {
            throw new NotFoundException("Payment ID Not Found!");
        }

        Tour tour = payment.getTour();
        if (tour.getType().equals(TourType.REQUESTED_TOUR)) {
            Booking booking = new Booking();
            booking.setBookingId(generateBookingId());
            booking.setCustomer(payment.getCustomer());
            booking.setCreatedDate(LocalDateTime.now());
            booking.setNumberOfAttendances(tour.getMaxParticipants());
            booking.setPayment(payment);
            booking.setStatus(BookingStatus.UNCHECKED);

            Booking newBooking = bookingRepository.save(booking);

            return convertToBookingResponse(newBooking);
        } else {
            throw new NotFoundException("This Tour is not a requested Tour!");
        }
    }

    public BookingResponse searchBooking(String bookingId, String customerId, String tourId, String paymentId, BookingStatus bookingStatus) {
        return null;
    }

    public String generateBookingId() {
        Booking lastBooking = bookingRepository.findTopByOrderByIdDesc();
        int lastId = 0;
        if (lastBooking != null && lastBooking.getBookingId() != null) {
            String lastBookingId = lastBooking.getBookingId();
            lastId = Integer.parseInt(lastBookingId.substring(1));
        }

        return "B" + (lastId + 1);
    }

    public BookingResponse convertToBookingResponse(Booking booking) {
        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setBookingId(booking.getBookingId());
        bookingResponse.setPaymentId(booking.getPayment().getPaymentId());
        bookingResponse.setTourId(booking.getTour().getTourId());
        bookingResponse.setCustomerId(booking.getCustomer().getUserId());
        bookingResponse.setCreatedDate(booking.getCreatedDate());
        bookingResponse.setNumberOfAttendances(booking.getNumberOfAttendances());

        return bookingResponse;
    }
}

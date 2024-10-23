package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.entity.Booking;
import com.project.KoiBookingSystem.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking findBookingByBookingId(String bookingId);


    List<Booking> findByTour_TourId(String tourId);

    @Query("SELECT b FROM Booking b WHERE b.isExpired = false AND b.payment IS NULL AND b.createdDate <= :expiredTime")
    List<Booking> findBookingsToExpire(LocalDateTime expiredTime);

    List<Booking> findBySalesIsNullAndIsExpiredFalse();

    List<Booking> findByTour_Consulting_UserId(String consultingId);

    Optional<Object> findByBookingId(String bookingID);//.


    List<Booking> findBookingsByCustomer(Account account);//

    List<Booking> findBookingsByIsExpiredAndTour_TourId(boolean isExpired, String tourId);//.
}
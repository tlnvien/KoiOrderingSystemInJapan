package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.entity.Booking;
import com.project.KoiBookingSystem.enums.RequestStatus;
import com.project.KoiBookingSystem.enums.TourType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking findBookingByBookingId(String bookingId);

    List<Booking> findByIsExpiredFalse();

    List<Booking> findByIsExpiredTrue();

    List<Booking> findByTour_TourId(String tourId);

    @Query("SELECT b FROM Booking b WHERE b.isExpired = false AND b.payment IS NULL AND b.createdDate <= :expiredTime AND b.tour.type = :type")
    List<Booking> findBookingsToExpire(LocalDateTime expiredTime, TourType type);

    @Query("SELECT b FROM Booking b WHERE b.isExpired = false AND b.payment IS NULL AND b.requestStatus = :status")
    List<Booking> findByRequestStatusAndIsExpiredFalse(RequestStatus status);

    List<Booking> findByTour_Consulting_UserId(String consultingId);

    Optional<Object> findByBookingId(String bookingID);//.

    List<Booking> findBookingsByCustomer(Account account);//

    List<Booking> findBookingsByIsExpiredAndTour_TourId(boolean isExpired, String tourId);

}

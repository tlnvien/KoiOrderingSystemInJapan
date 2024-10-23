package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Booking;
import com.project.KoiBookingSystem.enums.TourType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking findBookingByBookingId(String bookingId);

    List<Booking> findByIsExpiredFalse();

    List<Booking> findByIsExpiredTrue();

    List<Booking> findByTour_TourId(String tourId);

    @Query("SELECT b FROM Booking b WHERE b.isExpired = false AND b.payment IS NULL AND b.createdDate <= :expiredTime AND b.tour.type = :type")
    List<Booking> findBookingsToExpire(LocalDateTime expiredTime, TourType type);

    List<Booking> findBySalesIsNullAndIsExpiredFalse();

    List<Booking> findByTour_Consulting_UserId(String consultingId);
}

package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.entity.Booking;
import com.project.KoiBookingSystem.enums.BookingStatus;
import com.project.KoiBookingSystem.enums.RequestStatus;
import com.project.KoiBookingSystem.enums.TourType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking findBookingByBookingId(String bookingId);

    List<Booking> findBySales_UserId(String userId);

    List<Booking> findByIsExpiredFalse();

    List<Booking> findByIsExpiredTrue();

    @Query("SELECT b FROM Booking b WHERE b.tour.tourId = :tourId AND b.isExpired = false AND b.bookingStatus <> com.project.KoiBookingSystem.enums.BookingStatus.CANCELLED")
    List<Booking> findByTour_TourIdAndIsExpiredFalse(String tourId);

    @Query("SELECT b FROM Booking b WHERE b.isExpired = false AND b.payment IS NULL AND b.createdDate <= :expiredTime AND b.tour.type = :type")
    List<Booking> findBookingsToExpire(LocalDateTime expiredTime, TourType type);

    @Query("SELECT b FROM Booking b WHERE b.isExpired = false AND b.payment IS NULL AND b.requestStatus = :status")
    List<Booking> findByRequestStatusAndIsExpiredFalse(RequestStatus status);

    List<Booking> findByTour_Consulting_UserId(String consultingId);

    Optional<Object> findByBookingId(String bookingID);//.

    List<Booking> findBookingsByCustomer(Account account);//

    List<Booking> findBookingsByIsExpiredAndTour_TourId(boolean isExpired, String tourId);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.bookingStatus = :status")
    long countByBookingStatus(@Param("status") BookingStatus status);

    // Truy vấn đếm số lượng booking CHECKED cho một tour có sẵn dựa trên tourId
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.bookingStatus = :status AND b.tour.tourId = :tourId AND b.tour.type = :type")
    long countCheckedBookingsForAvailableTour(@Param("status") BookingStatus status, @Param("tourId") String tourId, @Param("type") TourType type);


    // Truy vấn đếm số lượng booking CHECKED cho một tour có sẵn dựa trên tourId
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.bookingStatus = :status AND b.tour.tourId = :tourId AND b.tour.type = :type")
    long countCheckedBookingsForRequestTour(@Param("status") BookingStatus status, @Param("tourId") String tourId, @Param("type") TourType type);
}

package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.entity.Booking;
import com.project.KoiBookingSystem.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Booking findTopByOrderByIdDesc();


    @Query("SELECT SUM(b.seatBooked) FROM Booking b")
    Integer sumSeatBooked();

    Optional<Booking> findByBookingID(String bookingID);

}


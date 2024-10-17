package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking findBookingByBookingId(UUID bookingId);

    List<Booking> findByIsExpiredFalse();

    List<Booking> findByIsExpiredTrue();
}

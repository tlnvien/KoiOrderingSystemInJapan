package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking findTopByOrderByIdDesc();
}

package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.BookingDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingDetailRepository extends JpaRepository<BookingDetail, Long> {
}

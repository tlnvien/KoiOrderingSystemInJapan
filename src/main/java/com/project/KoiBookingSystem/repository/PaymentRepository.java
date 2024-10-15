package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}

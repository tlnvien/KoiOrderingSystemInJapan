package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Payment;
import com.project.KoiBookingSystem.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Payment findTopByOrderByIdDesc();
}

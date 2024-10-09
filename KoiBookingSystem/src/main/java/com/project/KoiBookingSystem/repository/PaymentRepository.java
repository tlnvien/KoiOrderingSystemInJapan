package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Payment;
import com.project.KoiBookingSystem.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Payment findTopByOrderByIdDesc();

    Payment findByPaymentId(String paymentId);

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByCustomer_UserId(String customerId);

    List<Payment> findByCustomer_UserIdAndStatus(String customerId, PaymentStatus status);

    Payment findByPaymentIdAndCustomer_UserId(String paymentId, String customerId);
}

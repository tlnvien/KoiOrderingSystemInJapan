package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Payment;
import com.project.KoiBookingSystem.enums.PaymentStatus;
import com.project.KoiBookingSystem.enums.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Payment findTopByOrderByIdDesc();

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status AND p.paymentType = :type")
    Long countPaymentsByStatusAndType(@Param("status") PaymentStatus status, @Param("type") PaymentType type);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status AND p.paymentType = :type")
    Long countPaymentsDeliveringByStatusAndType(@Param("status") PaymentStatus status, @Param("type") PaymentType type);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status AND p.paymentType = :type")
    Long countPaymentsToursByStatusAndType(@Param("status") PaymentStatus status, @Param("type") PaymentType type);
}

package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.OrdersPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderPaymentRepository extends JpaRepository<OrdersPayment, Long> {
}

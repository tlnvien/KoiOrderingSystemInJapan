package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}

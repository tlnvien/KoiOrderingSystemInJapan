package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    @Query("SELECT o FROM OrderDetail o WHERE o.orders.orderId = :orderId")
    List<OrderDetail> findAllByOrderId(@Param("orderId") String orderId);
}

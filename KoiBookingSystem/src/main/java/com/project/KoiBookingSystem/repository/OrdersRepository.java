package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    Orders findByOrderId(String orderId);

    List<Orders> findByCustomer_UserId(String customerId);

    List<Orders> findByTour_TourId(String tourId);
}

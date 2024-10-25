package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ordersRepository extends JpaRepository<Orders, UUID>{
    List<Orders> findOrderssByCustomer(Account customer);

    @Override
    List<Orders> findAll();

    List<Orders> findByTour_TourId(String tourId);
}

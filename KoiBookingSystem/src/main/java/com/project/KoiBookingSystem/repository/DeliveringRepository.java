package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Delivering;
import com.project.KoiBookingSystem.entity.Orders;
import com.project.KoiBookingSystem.enums.DeliveringStatus;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeliveringRepository extends JpaRepository<Delivering, Long> {

//    @Query("SELECT o FROM Orders o WHERE o.orderId IN :orderIds")
//    List<Orders> findByOrderIdIn(@Param("orderIds") List<String> orderId);

    Delivering findByDeliveringId(String deliveringId);

    List<Delivering> findByStatus(DeliveringStatus status);
}

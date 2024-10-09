package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Request;
import com.project.KoiBookingSystem.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Request findTopByOrderByIdDesc();

    Request findByRequestIdAndStatus(String requestId, RequestStatus status);

    @Query("SELECT r FROM Request r " +
            "WHERE (:requestId IS NULL OR r.requestId LIKE CONCAT('%', :requestId, '%')) " +
            "AND (:customerId IS NULL OR r.customer.userId = :customerId) " +
            "AND (:status IS NULL OR r.status = :status)")
    List<Request> searchRequests(@Param("requestId") String requestId, @Param("customerId") String customerId, @Param("status") RequestStatus status);

}

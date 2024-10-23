package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Request findTopByOrderByIdDesc();

    List<Request> findByDoneFalse();

    List<Request> findByDoneTrue();

    List<Request> findByFarmHost_UserId(String farmHostId);

    Request findByRequestId(String requestId);
}

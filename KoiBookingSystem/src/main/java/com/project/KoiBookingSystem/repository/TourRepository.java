package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Tour;
import com.project.KoiBookingSystem.model.response.TourResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TourRepository extends JpaRepository<Tour, Long> {

    Tour findTourByTourID(String tourID);

    List<Tour> findTourByStatusTrue();
}

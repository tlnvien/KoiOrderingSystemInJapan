package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Tour;
import com.project.KoiBookingSystem.entity.TourSchedule;
import com.project.KoiBookingSystem.model.response.TourScheduleResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TourScheduleRepository extends JpaRepository<TourSchedule, Long> {

    List<TourSchedule> findByTour(Tour tour);

    List<TourSchedule> findByStatusTrue();
}

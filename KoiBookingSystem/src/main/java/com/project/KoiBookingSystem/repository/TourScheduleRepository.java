package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Farm;
import com.project.KoiBookingSystem.entity.Tour;
import com.project.KoiBookingSystem.entity.TourSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TourScheduleRepository extends JpaRepository<TourSchedule, Long> {

    TourSchedule findByTour_TourIdAndFarm_FarmId(String tourId, String farmId);

}

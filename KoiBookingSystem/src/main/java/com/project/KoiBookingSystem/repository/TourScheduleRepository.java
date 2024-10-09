package com.project.KoiBookingSystem.repository;

<<<<<<< HEAD
import com.project.KoiBookingSystem.entity.Farm;
import com.project.KoiBookingSystem.entity.Tour;
import com.project.KoiBookingSystem.entity.TourSchedule;
=======
import com.project.KoiBookingSystem.entity.Tour;
import com.project.KoiBookingSystem.entity.TourSchedule;
import com.project.KoiBookingSystem.model.response.TourScheduleResponse;
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TourScheduleRepository extends JpaRepository<TourSchedule, Long> {

<<<<<<< HEAD
    TourSchedule findByTour_TourIdAndFarm_FarmId(String tourId, String farmId);

=======
    List<TourSchedule> findByTour(Tour tour);

    List<TourSchedule> findByStatusTrue();
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
}

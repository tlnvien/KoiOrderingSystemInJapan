package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.FarmImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FarmImageRepository extends JpaRepository<FarmImage, Long> {
    List<FarmImage> findFarmImageByStatusTrue();

<<<<<<< HEAD
    List<FarmImage> findByFarm_FarmId(String farmId);
=======
    List<FarmImage> findByFarm_FarmID(String farmID);
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec

}

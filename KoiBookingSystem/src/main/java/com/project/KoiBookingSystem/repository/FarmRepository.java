package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Farm;
<<<<<<< HEAD
=======
import com.project.KoiBookingSystem.model.response.FarmResponse;
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FarmRepository extends JpaRepository<Farm, Long> {

<<<<<<< HEAD
    Farm findFarmByFarmId(String farmId);
=======
    Farm findFarmByFarmID(String farmID);
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec

    List<Farm> findFarmByStatusTrue();
}

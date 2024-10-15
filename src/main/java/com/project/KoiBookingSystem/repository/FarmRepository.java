package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Farm;
import com.project.KoiBookingSystem.model.response.FarmResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FarmRepository extends JpaRepository<Farm, Long> {

    Farm findFarmByFarmID(String farmID);

    List<Farm> findFarmByStatusTrue();
}

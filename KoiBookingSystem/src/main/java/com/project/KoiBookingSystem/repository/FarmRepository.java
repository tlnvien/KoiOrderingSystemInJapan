package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Farm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FarmRepository extends JpaRepository<Farm, Long> {

    Farm findFarmByFarmId(String farmId);

    List<Farm> findFarmByStatusTrue();

    Farm findTopByOrderByIdDesc();

    Farm findByFarmNameAndStatusTrue(String farmName);

}

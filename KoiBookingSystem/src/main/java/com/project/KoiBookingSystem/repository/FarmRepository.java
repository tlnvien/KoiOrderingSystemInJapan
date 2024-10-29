package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Farm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FarmRepository extends JpaRepository<Farm, Long> {

    Farm findFarmByFarmIdAndStatusTrue(String farmId);

    Farm findByFarmNameAndStatusTrue(String farmName);

    List<Farm> findFarmByStatusTrue();

    Farm findTopByOrderByIdDesc();

    List<Farm> findByFarmNameContainingAndKoiFarmsKoiSpeciesContainingAndStatusTrue(String farmName, String species);

    List<Farm> findByFarmNameContainingAndStatusTrue(String farmName);

    List<Farm> findByKoiFarmsKoiSpeciesContainingAndStatusTrue(String species);

    Farm findByFarmId(String farmId);
}

package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Koi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KoiRepository extends JpaRepository<Koi, Long> {

    Koi findKoiByKoiIdAndStatusTrue(String koiId);

    Koi findBySpeciesAndStatusTrue(String species);

    List<Koi> findKoiByStatusTrue();

    Koi findTopByOrderByIdDesc();

    List<Koi> findBySpeciesContainingAndKoiFarmsFarmFarmNameContainingAndStatusTrue(String species, String farmId);

    List<Koi> findKoisBySpeciesContainingAndStatusTrue(String species);

    List<Koi> findByKoiFarmsFarmFarmNameContainingAndStatusTrue(String farmId);

}

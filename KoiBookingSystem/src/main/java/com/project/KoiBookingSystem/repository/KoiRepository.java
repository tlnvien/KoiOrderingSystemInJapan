package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Koi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KoiRepository extends JpaRepository<Koi, Long> {

    Koi findKoiByKoiId(String koiId);

    List<Koi> findKoiByStatusTrue();

    Koi findTopByOrderByIdDesc();

    List<Koi> findBySpeciesAndKoiFarmsFarmFarmNameAndStatusTrue(String species, String farmId);

    List<Koi> findKoisBySpeciesAndStatusTrue(String species);

    List<Koi> findByKoiFarmsFarmFarmNameAndStatusTrue(String farmId);

    Koi findBySpeciesAndStatusTrue(String species);
}

package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Farm;
import com.project.KoiBookingSystem.entity.Koi;
import com.project.KoiBookingSystem.entity.KoiFarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KoiFarmRepository extends JpaRepository<KoiFarm, Long> {

    @Query("SELECT kf.koi FROM KoiFarm kf WHERE kf.farm.farmId = :farmId AND kf.status = true")
    List<Koi> findKoiByFarmId(@Param("farmId") String farmId);

    @Query("SELECT kf.farm FROM KoiFarm kf WHERE kf.koi.koiId = :koiId AND kf.status = true")
    List<Farm> findFarmByKoiId(@Param("koiId") String koiId);

    KoiFarm findByFarm_farmIdAndKoi_koiId(String farmId, String koiId);
}

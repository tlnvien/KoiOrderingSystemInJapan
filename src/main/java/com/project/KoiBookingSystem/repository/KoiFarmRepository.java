package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Farm;
import com.project.KoiBookingSystem.entity.Koi;
import com.project.KoiBookingSystem.entity.KoiFarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KoiFarmRepository extends JpaRepository<KoiFarm, Long> {

    @Query("SELECT kf.koi FROM KoiFarm kf WHERE kf.farm.farmID = :farmID AND kf.status = true")
    List<Koi> findKoiByFarmID(@Param("farmID") String farmID);

    @Query("SELECT kf.farm FROM KoiFarm kf WHERE kf.koi.koiID = :koiID AND kf.status = true")
    List<Farm> findFarmByKoiID(@Param("koiID") String koiID);

    KoiFarm findByFarm_farmIDAndKoi_koiID(String farmID, String koiID);

    Farm findFarmByFarm_farmID(String farmID);

    Koi findKoiByFarm_farmIDAndKoiID(String farmID, String koiID);
}

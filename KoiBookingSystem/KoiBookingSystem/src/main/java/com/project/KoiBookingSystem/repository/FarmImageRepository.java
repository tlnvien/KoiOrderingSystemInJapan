package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.FarmImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FarmImageRepository extends JpaRepository<FarmImage, Long> {
    List<FarmImage> findByFarm_FarmId(String farmId);

}

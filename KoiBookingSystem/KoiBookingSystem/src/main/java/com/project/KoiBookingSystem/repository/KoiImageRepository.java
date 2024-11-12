package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.KoiImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KoiImageRepository extends JpaRepository<KoiImage, Long> {
    List<KoiImage> findByKoi_KoiId(String koiId);
    ;
}

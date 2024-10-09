package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Koi;
import com.project.KoiBookingSystem.model.response.KoiResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KoiRepository extends JpaRepository<Koi, Long> {

    Koi findKoiByKoiId(String koiId);

    List<Koi> findKoiByStatusTrue();
}

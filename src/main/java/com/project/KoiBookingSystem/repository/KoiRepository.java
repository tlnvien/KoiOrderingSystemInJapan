package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Koi;
import com.project.KoiBookingSystem.model.response.KoiResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface KoiRepository extends JpaRepository<Koi, UUID> {

    Koi findKoiByKoiID(String koiID);

    Koi findKoiById(UUID id);

    List<Koi> findKoiByStatusTrue();
}

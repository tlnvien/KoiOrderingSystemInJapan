package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Koi;
import com.project.KoiBookingSystem.model.response.KoiResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KoiRepository extends JpaRepository<Koi, Long> {

<<<<<<< HEAD
    Koi findKoiByKoiId(String koiId);
=======
    Koi findKoiByKoiID(String koiID);
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec

    List<Koi> findKoiByStatusTrue();
}

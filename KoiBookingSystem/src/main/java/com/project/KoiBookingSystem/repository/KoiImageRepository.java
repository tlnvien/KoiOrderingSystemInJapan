package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.KoiImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KoiImageRepository extends JpaRepository<KoiImage, Long> {
<<<<<<< HEAD
    List<KoiImage> findByKoi_KoiId(String koiId);
=======
    List<KoiImage> findByKoi_KoiID(String koiID);
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec

    List<KoiImage> findKoiImageByStatusTrue();
}

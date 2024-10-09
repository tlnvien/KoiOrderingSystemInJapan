package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TourRepository extends JpaRepository<Tour, Long> {

    Tour findTopByOrderByIdDesc();

    Tour findTourByTourId(String tourId);

    List<Tour> findTourByStatusTrue();

    @Query("SELECT DISTINCT t FROM Tour t " +
    "JOIN t.tourSchedules ts " +
    "JOIN ts.farm f " +
    "JOIN f.koiFarms kf " +
    "JOIN kf.koi k " +
    "WHERE (:tourName IS NULL OR t.tourName LIKE CONCAT ('%', :tourName, '%')) " +
    "AND (:farmName IS NULL OR f.farmName LIKE CONCAT ('%', :farmName, '%')) " +
    "AND (:koiSpecies IS NULL OR k.species LIKE CONCAT ('%', :koiSpecies, '%')) " +
    "AND (:departureDate IS NULL OR t.departureDate = :departureDate) " +
    "AND (:minPrice IS NULL OR t.price >= :minPrice) " +
    "AND (:maxPrice IS NULL OR t.price <= :maxPrice)")
    List<Tour> searchTours(@Param("tourName") String tourName, @Param("farmName") String farmName, @Param("koiSpecies") String koiSpecies,
                           @Param("departureDate")LocalDate departureDate, @Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

}

package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.entity.Tour;
import com.project.KoiBookingSystem.enums.TourApproval;
import com.project.KoiBookingSystem.enums.TourType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TourRepository extends JpaRepository<Tour, Long> {

    Tour findTopByOrderByIdDesc();

    Tour findTourByTourId(String tourId);

    @Query("SELECT t FROM Tour t WHERE t.status <> 'CANCELLED'")
    List<Tour> findAllByStatusExcludingCancelled();

    @Query("SELECT t FROM Tour t WHERE t.consulting = :consulting " +
            "AND t.status IN (com.project.KoiBookingSystem.enums.TourStatus.NOT_YET, com.project.KoiBookingSystem.enums.TourStatus.IN_PROGRESS) " +
            "AND t.endDate > :newTourDepartureDate")
    List<Tour> findActiveToursByConsultingAndEndDateAfter(@Param("consulting") Account consulting, @Param("newTourDepartureDate")LocalDate newTourDepartureDate);

    @Query("SELECT DISTINCT t FROM Tour t " +
    "JOIN t.tourSchedules ts " +
    "JOIN ts.farm f " +
    "LEFT JOIN f.koiFarms kf " +
    "LEFT JOIN kf.koi k " +
    "WHERE (:tourName IS NULL OR t.tourName LIKE CONCAT ('%', :tourName, '%')) " +
    "AND (:farmName IS NULL OR f.farmName LIKE CONCAT ('%', :farmName, '%')) " +
    "AND (:koiSpecies IS NULL OR k.species LIKE CONCAT ('%', :koiSpecies, '%')) " +
    "AND (:departureDate IS NULL OR t.departureDate = :departureDate) " +
    "AND (:minPrice IS NULL OR t.price >= :minPrice) " +
    "AND (:maxPrice IS NULL OR t.price <= :maxPrice)")
    List<Tour> searchTours(@Param("tourName") String tourName, @Param("farmName") String farmName, @Param("koiSpecies") String koiSpecies,
                           @Param("departureDate")LocalDate departureDate, @Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);



    List<Tour> findByTypeAndTourApproval(TourType type, TourApproval approval);
}

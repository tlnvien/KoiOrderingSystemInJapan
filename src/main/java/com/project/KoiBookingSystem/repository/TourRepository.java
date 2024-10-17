package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TourRepository extends JpaRepository<Tour, Long> {

    Tour findTourByTourID(String tourID);

    List<Tour> findTourByStatusTrue();

    @Query("SELECT t FROM Tour t WHERE "
            + "( :destination IS NULL OR t.tourName = :destination ) AND "
            + "( :minPrice IS NULL OR t.price >= :minPrice ) AND "
            + "( :maxPrice IS NULL OR t.price <= :maxPrice ) AND "
            + "( :startDate IS NULL OR t.startDate = :startDate )")
    List<Tour> findToursByFilters(@Param("destination") String destination,
                                  @Param("minPrice") Double minPrice,
                                  @Param("maxPrice") Double maxPrice,
                                  @Param("startDate") LocalDate startDate);

    List<Tour> findToursByTourID(String tourID);


//    List<Booking> findBookingsByTourID(String tourID);
}


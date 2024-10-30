package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.entity.Tour;
import com.project.KoiBookingSystem.enums.BookingStatus;
import com.project.KoiBookingSystem.enums.TourApproval;
import com.project.KoiBookingSystem.enums.TourType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Query("SELECT t FROM Tour t WHERE t.type = :type AND t.status <> com.project.KoiBookingSystem.enums.TourStatus.CANCELLED")
    List<Tour> findByTypeAndStatusNotCancelled(@Param("type") TourType type);

    @Query("SELECT t FROM Tour t WHERE t.consulting.userId = :consultingId AND t.status <> com.project.KoiBookingSystem.enums.TourStatus.CANCELLED")
    List<Tour> findByConsulting_UserId(@Param("consultingId") String consultingId);

    //tổng số tour trong ngày
    @Query("SELECT COUNT(t) FROM Tour t WHERE t.departureDate = CURRENT_DATE")
    long countToursToday();

    //tổng số tour trong tuần
    @Query("SELECT COUNT(t) FROM Tour t WHERE t.departureDate BETWEEN :startOfWeek AND :endOfWeek")
    long countToursThisWeek(@Param("startOfWeek") LocalDate startOfWeek, @Param("endOfWeek") LocalDate endOfWeek);

    // Đếm số tour diễn ra trong năm cụ thể
    @Query("SELECT COUNT(t) FROM Tour t WHERE t.departureDate BETWEEN :startOfYear AND :endOfYear")
    long countToursThisYear(@Param("startOfYear") LocalDate startOfYear, @Param("endOfYear") LocalDate endOfYear);


    @Query("SELECT COUNT(t) FROM Tour t WHERE t.departureDate BETWEEN :startOfMonth AND :endOfMonth")
    long countToursThisMonth(@Param("startOfMonth") LocalDate startOfMonth, @Param("endOfMonth") LocalDate endOfMonth);

    // Đếm số lượng tour theo userID của nhân viên tư vấn
    @Query("SELECT COUNT(b) FROM Tour b WHERE b.consulting.userId = :userId")
    long countToursByConsultantUserId(String userId);

    // Đếm số lượng tour đã đặt bởi một khách hàng trong tháng với trạng thái CHECKED
    @Query("SELECT COUNT(b) FROM Booking b " +
            "WHERE b.createdDate >= :startDate AND b.createdDate <= :endDate " +
            "AND b.customer.userId = :userID " +
            "AND b.bookingStatus = :bookingStatus")
    long countToursByCustomerInMonth(@Param("userID") String userID,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate,
                                     @Param("bookingStatus") BookingStatus bookingStatus);


    // Lấy danh sách khách hàng đặt nhiều tour nhất trong tháng với trạng thái CHECKED.
    @Query("SELECT b.customer.userId, COUNT(b) AS tourCount " +
            "FROM Booking b " +
            "WHERE b.createdDate >= :startDate AND b.createdDate <= :endDate " +
            "AND b.bookingStatus = :bookingStatus " +
            "GROUP BY b.customer.userId " +
            "ORDER BY tourCount DESC")
    List<Object[]> getTopCustomersInMonth(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate,
                                          @Param("bookingStatus") BookingStatus bookingStatus);

    // Đếm số lượng tour theo userID của nhân viên tư vấn
    @Query("SELECT COUNT(b) FROM Tour b WHERE b.sales.userId = :userId")
    long countToursBySaleUserId(String userId);
}


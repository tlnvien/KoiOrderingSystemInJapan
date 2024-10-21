package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.entity.Tour;
import com.project.KoiBookingSystem.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    //tổng số tour trong ngày
    @Query("SELECT COUNT(t) FROM Tour t WHERE t.startDate = CURRENT_DATE")
    long countToursToday();

    //tổng số tour trong tuần
    @Query("SELECT COUNT(t) FROM Tour t WHERE t.startDate BETWEEN :startOfWeek AND :endOfWeek")
    long countToursThisWeek(@Param("startOfWeek") LocalDate startOfWeek, @Param("endOfWeek") LocalDate endOfWeek);

    //tổng số tour trong quý
    @Query("SELECT COUNT(t) FROM Tour t WHERE QUARTER(t.startDate) = :currentQuarter AND YEAR(t.startDate) = :currentYear")
    long countToursThisQuarter(@Param("currentQuarter") int currentQuarter, @Param("currentYear") int currentYear);

    // Đếm số tour diễn ra trong năm cụ thể
    @Query("SELECT COUNT(t) FROM Tour t WHERE t.startDate BETWEEN :startOfYear AND :endOfYear")
    long countToursThisYear(@Param("startOfYear") LocalDate startOfYear, @Param("endOfYear") LocalDate endOfYear);


    // Tính số tour trong tháng hiện tại
    @Query("SELECT COUNT(t) FROM Tour t WHERE t.startDate BETWEEN :startOfMonth AND :endOfMonth")
    long countToursThisMonth(LocalDate startOfMonth, LocalDate endOfMonth);

    // Đếm số lượng tour theo userID của nhân viên tư vấn
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.consulting.userID = :userID")
    long countToursByConsultantUserId(String userID);

    // Đếm số lượng tour đã đặt bởi một khách hàng trong tháng với trạng thái CHECKED
    @Query("SELECT COUNT(b) FROM Booking b " +
            "WHERE b.createDate >= :startDate AND b.createDate <= :endDate " +
            "AND b.customer.userID = :userID " +
            "AND b.bookingStatus = :bookingStatus")
    long countToursByCustomerInMonth(@Param("userID") String userID,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate,
                                     @Param("bookingStatus") BookingStatus bookingStatus);


    // Lấy danh sách khách hàng đặt nhiều tour nhất trong tháng với trạng thái CHECKED
    @Query("SELECT b.customer.userID, COUNT(b) AS tourCount " +
            "FROM Booking b " +
            "WHERE b.createDate >= :startDate AND b.createDate <= :endDate " +
            "AND b.bookingStatus = :bookingStatus " +
            "GROUP BY b.customer.userID " +
            "ORDER BY tourCount DESC")
    List<Object[]> getTopCustomersInMonth(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate,
                                          @Param("bookingStatus") BookingStatus bookingStatus);

    Tour findTourByConsulting(Account consulting);
}


//    List<Booking> findBookingsByTourID(String tourID);



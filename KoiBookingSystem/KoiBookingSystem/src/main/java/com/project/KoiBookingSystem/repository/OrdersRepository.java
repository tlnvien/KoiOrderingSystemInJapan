package com.project.KoiBookingSystem.repository;

import com.project.KoiBookingSystem.entity.Orders;
import com.project.KoiBookingSystem.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    Orders findByOrderIdAndExpiredFalse(String orderId);

    List<Orders> findByCustomer_UserIdAndExpiredFalse(String customerId);

    List<Orders> findByTour_TourIdAndExpiredFalse(String tourId);

    List<Orders> findByDelivering_DeliveringIdAndExpiredFalse(String deliveringId);

    List<Orders> findByFarms_FarmId(String farmId);

    List<Orders> findByStatusAndDeliveringIsNull(OrderStatus status);

    @Query("SELECT o FROM Orders o WHERE o.customer.address LIKE CONCAT ('%', :address, '%') AND o.expired = false AND o.status = com.project.KoiBookingSystem.enums.OrderStatus.RECEIVED")
    List<Orders> findByCustomer_AddressAndExpiredFalseAndStatusReceived(@Param("address") String address);

    @Query("SELECT o FROM Orders o WHERE o.status = :status AND o.expired = false")
    List<Orders> findUnpaidOrders(@Param("status") OrderStatus status);


    // Đếm số lượng đơn hàng có trạng thái DELIVERED
    @Query("SELECT COUNT(o) FROM Orders o WHERE o.status = :status")
    long countOrdersByStatus(@Param("status") OrderStatus status);

    // Đếm tổng số đơn hàng DELIVERED trong ngày
    @Query("SELECT COUNT(o) FROM Orders o WHERE DATE(o.deliveredDate) = CURRENT_DATE AND o.status = :status")
    long countDeliveredOrdersToday(@Param("status") OrderStatus status);

    // Đếm tổng số đơn hàng DELIVERED trong tuần
    @Query("SELECT COUNT(o) FROM Orders o WHERE o.deliveredDate BETWEEN :startOfWeek AND :endOfWeek AND o.status = :status")
    long countDeliveredOrdersThisWeek(@Param("startOfWeek") LocalDateTime startOfWeek, @Param("endOfWeek") LocalDateTime endOfWeek, @Param("status") OrderStatus status);

    // Đếm tổng số đơn hàng DELIVERED trong tháng
    @Query("SELECT COUNT(o) FROM Orders o WHERE o.deliveredDate BETWEEN :startOfMonth AND :endOfMonth AND o.status = :status")
    long countDeliveredOrdersThisMonth(@Param("startOfMonth") LocalDateTime startOfMonth, @Param("endOfMonth") LocalDateTime endOfMonth, @Param("status") OrderStatus status);

    // Đếm tổng số đơn hàng DELIVERED trong năm
    @Query("SELECT COUNT(o) FROM Orders o WHERE o.deliveredDate BETWEEN :startOfYear AND :endOfYear AND o.status = :status")
    long countDeliveredOrdersThisYear(@Param("startOfYear") LocalDateTime startOfYear, @Param("endOfYear") LocalDateTime endOfYear, @Param("status") OrderStatus status);


    // Đếm số lượng đơn hàng của một khách hàng theo userID trong một tháng
    @Query("SELECT COUNT(o) FROM Orders o " +
            "WHERE o.deliveredDate BETWEEN :startDate AND :endDate " +
            "AND o.customer.userId = :userId")
    long countOrdersByCustomerInMonth(@Param("userId") String userId,
                                      @Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);

    // Lấy danh sách khách hàng đặt nhiều đơn hàng nhất trong tháng
    @Query("SELECT o.customer.userId, COUNT(o) AS orderCount " +
            "FROM Orders o " +
            "WHERE o.deliveredDate BETWEEN :startDate AND :endDate " +
            "GROUP BY o.customer.userId " +
            "ORDER BY orderCount DESC")
    List<Object[]> getTopCustomersInMonth(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);
}
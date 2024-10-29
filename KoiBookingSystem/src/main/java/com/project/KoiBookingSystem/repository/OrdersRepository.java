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

    // Đếm tổng số đơn hàng trong ngày
    @Query("SELECT COUNT(o) FROM Orders o WHERE o.orderDate = CURRENT_DATE")
    long countOrdersToday();

    // Đếm tổng số đơn hàng trong tuần
    @Query("SELECT COUNT(o) FROM Orders o WHERE o.orderDate BETWEEN :startOfWeek AND :endOfWeek")
    long countOrdersThisWeek(@Param("startOfWeek") LocalDateTime startOfWeek, @Param("endOfWeek") LocalDateTime endOfWeek);

    // Đếm tổng số đơn hàng trong tháng
    @Query("SELECT COUNT(o) FROM Orders o WHERE o.orderDate BETWEEN :startOfMonth AND :endOfMonth")
    long countOrdersThisMonth(@Param("startOfMonth") LocalDateTime startOfMonth, @Param("endOfMonth") LocalDateTime endOfMonth);

    // Đếm tổng số đơn hàng trong năm cụ thể
    @Query("SELECT COUNT(o) FROM Orders o WHERE o.orderDate BETWEEN :startOfYear AND :endOfYear")
    long countOrdersThisYear(@Param("startOfYear") LocalDateTime startOfYear, @Param("endOfYear") LocalDateTime endOfYear);

    // Đếm số lượng đơn hàng của một khách hàng theo userID trong một tháng
    @Query("SELECT COUNT(o) FROM Orders o " +
            "WHERE o.orderDate BETWEEN :startDate AND :endDate " +
            "AND o.customer.userId = :userId")
    long countOrdersByCustomerInMonth(@Param("userId") String userId,
                                      @Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);

    // Lấy danh sách khách hàng đặt nhiều đơn hàng nhất trong tháng
    @Query("SELECT o.customer.userId, COUNT(o) AS orderCount " +
            "FROM Orders o " +
            "WHERE o.orderDate BETWEEN :startDate AND :endDate " +
            "GROUP BY o.customer.userId " +
            "ORDER BY orderCount DESC")
    List<Object[]> getTopCustomersInMonth(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);
}

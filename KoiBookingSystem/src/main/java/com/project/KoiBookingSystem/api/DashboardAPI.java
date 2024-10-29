package com.project.KoiBookingSystem.api;

import com.project.KoiBookingSystem.service.DashboardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class DashboardAPI {

    @Autowired
    DashboardService dashboardService;

    // Tổng số tour trong ngày hiện tại // check
    @GetMapping("/countTour/today")
    public ResponseEntity<Long> countToursToday() {
        long count = dashboardService.getToursToday();
        return ResponseEntity.ok(count);
    }

    // Tổng số tour trong tuần hiện tại // check
    @GetMapping("/countTour/week")
    public ResponseEntity<Long> countToursThisWeek() {
        long count = dashboardService.getToursThisWeek();
        return ResponseEntity.ok(count);
    }

    // Tổng số tour trong tháng cụ thể //check
    @GetMapping("/countTour/month")
    public ResponseEntity<Long> countToursInMonth(@RequestParam int year, @RequestParam int month) {
        long count = dashboardService.getToursInMonth(year, month);
        return ResponseEntity.ok(count);
    }

    // Tổng số tour trong năm cụ thể // check
    @GetMapping("/countTour/year")
    public ResponseEntity<Long> countToursInYear(@RequestParam int year) {
        long count = dashboardService.getToursInYear(year);
        return ResponseEntity.ok(count);
    }

    // Số tiền hệ thống đang có (Admin) /check
    @GetMapping("/balance")
    @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('ADMIN')")
    public ResponseEntity<Float> getBalance() {
        float balance = dashboardService.getBalanceByUserID();
        return ResponseEntity.ok(balance);
    }

    // Số lượng tour nhân viên tư vấn nhận (Consulting) // check
    @GetMapping("/consultantTour/count")
    @PreAuthorize("hasAuthority('CONSULTING')")
    public ResponseEntity<Long> countToursByConsultant() {
        long count = dashboardService.getToursByConsultantUserId();
        return ResponseEntity.ok(count);
    }

    // Số lượng tour đã đặt bởi một khách hàng trong tháng với trạng thái CHECKED // check
    @GetMapping("/count/customerTour/month")
    public ResponseEntity<Long> countToursByCustomerInMonth(@RequestParam String userID,
                                                            @RequestParam int year,
                                                            @RequestParam int month) {
        long count = dashboardService.getToursByCustomerInMonth(userID, year, month);
        return ResponseEntity.ok(count);
    }

    // Danh sách khách hàng đặt nhiều tour nhất trong tháng với trạng thái CHECKED // check
    @GetMapping("/count/top-customersTour/month")
    public ResponseEntity<List<Object[]>> getTopCustomersInMonth(@RequestParam int year,
                                                                 @RequestParam int month) {
        List<Object[]> topCustomers = dashboardService.getTopCustomersToursInMonth(year, month);
        return ResponseEntity.ok(topCustomers);
    }

    // Thống kê tổng quan các staff đăng kí (Manager) // check
    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<Map<String, Object>> getDashboardStats(){
        Map<String, Object> stats = dashboardService.getDashBoardStats();
        return ResponseEntity.ok(stats);
    }

    // Số lượng tour mà nhân viên bán hàng (SALE) nhận // check
    @GetMapping("/saleTour/count")
    @PreAuthorize("hasAuthority('SALES')")
    public ResponseEntity<Long> countToursBySale() {
        long count = dashboardService.getToursBySaleUserId();
        return ResponseEntity.ok(count);
    }

    // API Đơn hàng

    // Số lượng đơn hàng có trạng thái DELIVERED
    @GetMapping("/countOrders/delivered")
    public ResponseEntity<Long> getDeliveredOrdersCount() {
        return ResponseEntity.ok(dashboardService.countDeliveredOrders());
    }

    // Số lượng đơn hàng trong ngày
    @GetMapping("/countOrders/today")
    public ResponseEntity<Long> getOrdersTodayCount() {
        return ResponseEntity.ok(dashboardService.countOrdersToday()); // check
    }

    // Số lượng đơn hàng trong tuần
    @GetMapping("/countOrders/week")
    public ResponseEntity<Long> getOrdersThisWeekCount() {
        return ResponseEntity.ok(dashboardService.countOrdersThisWeek());
    }

    // Số lượng đơn hàng trong tháng
    @GetMapping("/countOrders/month")
    public ResponseEntity<Long> getOrdersThisMonthCount() {
        return ResponseEntity.ok(dashboardService.countOrdersThisMonth());
    }

    // Số lượng đơn hàng trong năm
    @GetMapping("/countOrders/year")
    public ResponseEntity<Long> getOrdersThisYearCount() {
        return ResponseEntity.ok(dashboardService.countOrdersThisYear());
    }

    // Số lượng đơn hàng của một khách hàng trong tháng
    @GetMapping("/countOrders/customer/{userId}/{year}/{month}")
    public ResponseEntity<Long> getOrdersByCustomerInMonth(
            @PathVariable String userId,
            @PathVariable int year,
            @PathVariable int month) {
        return ResponseEntity.ok(dashboardService.countOrdersByCustomerInMonth(userId, year, month));
    }

    // Danh sách khách hàng đặt nhiều đơn hàng nhất trong tháng
    @GetMapping("/countOrders/top-customers/{year}/{month}")
    public ResponseEntity<List<Object[]>> getTopCustomersOrderInMonth(
            @PathVariable int year,
            @PathVariable int month) {
        return ResponseEntity.ok(dashboardService.getTopCustomersOrderInMonth(year, month));
    }
}

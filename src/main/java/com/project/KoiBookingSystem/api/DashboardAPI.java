package com.project.KoiBookingSystem.api;
import com.project.KoiBookingSystem.service.DashboardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("api/dashboard")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class DashboardAPI {

    @Autowired
    DashboardService dashboardService;

    // Tính tổng số tour trong ngày hiện tại
    @GetMapping("/count/today")
    public ResponseEntity<Long> countToursToday() {
        long count = dashboardService.getToursToday();
        return ResponseEntity.ok(count);
    }

    // Tính tổng số tour trong tuần hiện tại
    @GetMapping("/count/week")
    public ResponseEntity<Long> countToursThisWeek() {
        long count = dashboardService.getToursThisWeek();
        return ResponseEntity.ok(count);
    }

    // Tính tổng số tour trong tháng cụ thể
    @PutMapping("/count/month")
    public ResponseEntity<Long> countToursInMonth(@RequestParam int year, @RequestParam int month) {
        long count = dashboardService.getToursInMonth(year, month);
        return ResponseEntity.ok(count);
    }
    // Tính tổng số tour trong quý hiện tại
    @GetMapping("/count/quarter")
    public ResponseEntity<Long> countToursThisQuarter() {
        long count = dashboardService.getToursThisQuarter();
        return ResponseEntity.ok(count);
    }

    // Tính tổng số tour trong năm cụ thể
    @PutMapping("/count/year")
    public ResponseEntity<Long> countToursInYear(@RequestParam int year) {
        long count = dashboardService.getToursInYear(year);
        return ResponseEntity.ok(count);
    }

    //Số tiền hệ thống đang có
    @GetMapping("/balance")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Float> getBalance() {
        float balance = dashboardService.getBalanceByUserID();
        return ResponseEntity.ok(balance);
    }

    //số lượng tour nhân viên tư vấn nhận
    @PutMapping("/consultant/count")
    @PreAuthorize("hasAuthority('CONSULTING')")
    public ResponseEntity<Long> countToursByConsultant() {
        long count = dashboardService.getToursByConsultantUserId(); // Gọi phương thức trong service
        return ResponseEntity.ok(count);
    }

    // Lấy số lượng tour đã đặt bởi một khách hàng trong tháng với trạng thái CHECKED
    @PutMapping("/count/customer/month")
    public ResponseEntity<Long> countToursByCustomerInMonth(@RequestParam String userID,
                                                            @RequestParam int year,
                                                            @RequestParam int month) {

        long count = dashboardService.getToursByCustomerInMonth(userID, year, month);
        return ResponseEntity.ok(count);
    }

    // Lấy danh sách khách hàng đặt nhiều tour nhất trong tháng với trạng thái CHECKED
    @PutMapping("/count/top-customers/month")
    public ResponseEntity<List<Object[]>> getTopCustomersInMonth(@RequestParam int year,
                                                                 @RequestParam int month) {

        List<Object[]> topCustomers = dashboardService.getTopCustomersInMonth(year, month);
        return ResponseEntity.ok(topCustomers);
    }
}
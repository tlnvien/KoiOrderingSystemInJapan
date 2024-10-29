package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.enums.BookingStatus;
import com.project.KoiBookingSystem.enums.OrderStatus;
import com.project.KoiBookingSystem.enums.Role;
import com.project.KoiBookingSystem.repository.AccountRepository;
import com.project.KoiBookingSystem.repository.OrdersRepository;
import com.project.KoiBookingSystem.repository.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {
    @Autowired
    TourRepository tourRepository;

    @Autowired
    AuthenticationService authenticationService;


    @Autowired
    AccountRepository accountRepository;


    @Autowired
    OrdersRepository ordersRepository;

    // Dashboard thống kê chung
    public Map<String, Object> getDashBoardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Thống kê số lượng khách hàng
        long customersCount = accountRepository.countByRole(Role.CUSTOMER);
        stats.put("customersCount", customersCount);

        // Số lượng nhân viên tư vấn
        long consultingStaff = accountRepository.countByRole(Role.CONSULTING);
        stats.put("consultingStaff", consultingStaff);

        // Số lượng nhân viên sales
        long saleStaffCount = accountRepository.countByRole(Role.SALES);
        stats.put("saleStaff", saleStaffCount);

        // Số lượng manager
        long managerStaffCount = accountRepository.countByRole(Role.MANAGER);
        stats.put("managerStaff", managerStaffCount);

        long FarmHostCount = accountRepository.countByRole(Role.FARM_HOST);
        stats.put("farm host account", FarmHostCount);

        long DeliveringStaff = accountRepository.countByRole(Role.DELIVERING);
        stats.put("Delivering", DeliveringStaff);
        return stats;
    }

    //số lượng đơn hàng có trong tháng


    public long getToursToday() {
        return tourRepository.countToursToday();
    }

    //SL tour trong tuần
    public long getToursThisWeek() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);
        return tourRepository.countToursThisWeek(startOfWeek, endOfWeek);
    }

    // số lượng tour trong tháng
    public long getToursInMonth(int year, int month) {
        YearMonth selectedMonth = YearMonth.of(year, month);
        LocalDate startOfMonth = selectedMonth.atDay(1);
        LocalDate endOfMonth = selectedMonth.atEndOfMonth();
        return tourRepository.countToursThisMonth(startOfMonth, endOfMonth);
    }

    // tour trong năm
    public long getToursInYear(int year) {
        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        LocalDate endOfYear = LocalDate.of(year, 12, 31);
        return tourRepository.countToursThisYear(startOfYear, endOfYear);
    }

    // kiểm tra số tiền trong hệ thống
    public float getBalanceByUserID() {
        Account account = authenticationService.getCurrentAccount();
        if (account != null) {
            // Nếu tài khoản là ADMIN, trả về số dư của họ
            if (account.getRole() == Role.ADMIN) {
                return (float) account.getBalance();  // Trả về số dư của admin
            }
            // Nếu tài khoản là MANAGER, lấy số dư của admin
            else if (account.getRole() == Role.MANAGER) {
                // Giả sử bạn có một phương thức để lấy tài khoản admin
                Account adminAccount = accountRepository.findAccountByRole(Role.ADMIN); // Tìm tài khoản ADMIN
                if (adminAccount != null) {
                    return (float) adminAccount.getBalance();  // Trả về số dư của admin
                } else {
                    throw new RuntimeException("Tài khoản admin không tìm thấy!");
                }
            } else {
                throw new RuntimeException("Tài khoản phải là admin hoặc manager để xem số dư");
            }
        }
        throw new RuntimeException("Không tìm thấy tài khoản!");
    }



    // Phương thức mới để đếm số lượng tour của tư vấn viên dựa trên userID
    public long getToursByConsultantUserId() {
        Account account = authenticationService.getCurrentAccount();
        return tourRepository.countToursByConsultantUserId(account.getUserId());
    }

    // đếm số lượng só lượng tour mà thằng sales nhận dựa trên userID
    public long getToursBySaleUserId() {
        Account account = authenticationService.getCurrentAccount();
        return tourRepository.countToursBySaleUserId(account.getUserId());
    }


    //BOOKING - CUSTOMER.
    // Lấy số lượng tour đã đặt bởi một khách hàng trong tháng với trạng thái CHECKED
    public long getToursByCustomerInMonth(String userID, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);

        // Chuyển đổi LocalDate sang LocalDateTime
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay(); // 00:00:00 của ngày đầu tháng
        LocalDateTime endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59); // 23:59:59 của ngày cuối tháng

        // Truyền BookingStatus.CHECKED
        BookingStatus status = BookingStatus.CHECKED;

        return tourRepository.countToursByCustomerInMonth(userID, startDate, endDate, status);
    }

    // Lấy danh sách khách hàng đặt nhiều tour nhất trong tháng với trạng thái CHECKED
    public List<Object[]> getTopCustomersToursInMonth(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);

        // Chuyển đổi LocalDate sang LocalDateTime
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay(); // 00:00:00 của ngày đầu tháng
        LocalDateTime endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59); // 23:59:59 của ngày cuối tháng

        // Truyền BookingStatus.CHECKED
        BookingStatus status = BookingStatus.CHECKED;

        return tourRepository.getTopCustomersInMonth(startDate, endDate, status);
    }


    // DASH BOARD ĐƠN HÀNG

    // Đếm số lượng đơn hàng có trạng thái DELIVERED
    public long countDeliveredOrders() {
        return ordersRepository.countOrdersByStatus(OrderStatus.DELIVERED);
    }

    // Đếm tổng số đơn hàng trong ngày
    public long countOrdersToday() {
        return ordersRepository.countOrdersToday();
    }

    // Đếm tổng số đơn hàng trong tuần
    public long countOrdersThisWeek() {
        LocalDate today = LocalDate.now(); // Sử dụng LocalDate
        LocalDateTime startOfWeek = today.with(DayOfWeek.MONDAY).atStartOfDay(); // Chuyển sang LocalDateTime
        LocalDateTime endOfWeek = today.with(DayOfWeek.SUNDAY).atTime(23, 59, 59); // Chuyển sang LocalDateTime
        return ordersRepository.countOrdersThisWeek(startOfWeek, endOfWeek);
    }

    // Đếm tổng số đơn hàng trong tháng
    public long countOrdersThisMonth() {
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfMonth = LocalDate.from(currentMonth.atDay(1).atStartOfDay()).atStartOfDay(); // Chuyển LocalDate thành LocalDateTime
        LocalDateTime endOfMonth = LocalDate.from(currentMonth.atEndOfMonth().atTime(23, 59, 59)).atStartOfDay(); // Chuyển LocalDate thành LocalDateTime
        return ordersRepository.countOrdersThisMonth(startOfMonth, endOfMonth);
    }


    // Đếm tổng số đơn hàng trong năm
    public long countOrdersThisYear() {
        LocalDate startOfYear = LocalDate.of(LocalDate.now().getYear(), 1, 1);
        LocalDate endOfYear = LocalDate.of(LocalDate.now().getYear(), 12, 31);
        return ordersRepository.countOrdersThisYear(startOfYear.atStartOfDay(), endOfYear.atStartOfDay());
    }

    // Đếm số lượng đơn hàng của một khách hàng trong tháng
    public long countOrdersByCustomerInMonth(String userId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        return ordersRepository.countOrdersByCustomerInMonth(userId, startDate, endDate);
    }

    // Lấy danh sách khách hàng đặt nhiều đơn hàng nhất trong tháng
    public List<Object[]> getTopCustomersOrderInMonth(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        return ordersRepository.getTopCustomersInMonth(startDate, endDate);
    }

}

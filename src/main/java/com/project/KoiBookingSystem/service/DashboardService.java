package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.enums.BookingStatus;
import com.project.KoiBookingSystem.enums.Role;
import com.project.KoiBookingSystem.repository.AccountRepository;
import com.project.KoiBookingSystem.repository.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class DashboardService {

    @Autowired
    TourRepository tourRepository;

    @Autowired
    private AuthenticationService authenticationService;




    // dashboard của tour
    //SL tour trong ngày
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
    // SL tour trong quý
    public long getToursThisQuarter() {
        LocalDate today = LocalDate.now();
        int currentQuarter = (today.getMonthValue() - 1) / 3 + 1;
        int currentYear = today.getYear();
        return tourRepository.countToursThisQuarter(currentQuarter, currentYear);
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
            // Kiểm tra vai trò của tài khoản
            if (account.getRole() == Role.ADMIN) {
                return account.getBalance();  // Trả về giá trị balance
            } else {
                throw new RuntimeException("Account is not an Admin!");
            }
        }
        throw new RuntimeException("Account not found!");
    }

    // Phương thức mới để đếm số lượng tour của tư vấn viên dựa trên userID
    public long getToursByConsultantUserId() {
        Account account = authenticationService.getCurrentAccount();
        return tourRepository.countToursByConsultantUserId(account.getUserID());
    }


    //BOOKING - CUSTOMER
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
    public List<Object[]> getTopCustomersInMonth(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);

        // Chuyển đổi LocalDate sang LocalDateTime
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay(); // 00:00:00 của ngày đầu tháng
        LocalDateTime endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59); // 23:59:59 của ngày cuối tháng

        // Truyền BookingStatus.CHECKED
        BookingStatus status = BookingStatus.CHECKED;

        return tourRepository.getTopCustomersInMonth(startDate, endDate, status);
    }
}


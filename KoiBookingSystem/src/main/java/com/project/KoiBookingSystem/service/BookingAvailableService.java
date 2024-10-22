package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.*;
import com.project.KoiBookingSystem.enums.*;
import com.project.KoiBookingSystem.exception.ActionException;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.model.request.BookingAvailableRequest;
import com.project.KoiBookingSystem.model.response.*;
import com.project.KoiBookingSystem.repository.AccountRepository;
import com.project.KoiBookingSystem.repository.BookingRepository;
import com.project.KoiBookingSystem.repository.PaymentRepository;
import com.project.KoiBookingSystem.repository.TourRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class BookingAvailableService {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    ModelMapper modelMapper;


    @Autowired
    TourRepository tourRepository;


    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PaymentRepository paymentRepository;


    @Autowired
    EmailService emailService;

    @Autowired
    AuthenticationService authenticationService;

    @Transactional
    public BookingAvailableResponse createTicket(BookingAvailableRequest bookingRequest) {
        try {
            // Tìm tour theo ID
            Tour tour = tourRepository.findTourByTourId(bookingRequest.getTourID());
            if (tour == null) {
                throw new NotFoundException("Tour Not Found!");
            }

            // Kiểm tra xem khách hàng có tồn tại và có vai trò "CUSTOMER"
            Account currentCustomer = authenticationService.getCurrentAccount();
            if (currentCustomer == null || !currentCustomer.getRole().equals(Role.CUSTOMER)) {
                throw new NotFoundException("CUSTOMER Not Found!");
            }

            // Kiểm tra số ghế còn lại của tour
            int requestedSeats = bookingRequest.getNumberOfAttendances();
            if (tour.getRemainSeat() < requestedSeats) {
                throw new ActionException("Not enough seats available!");
            }


            // Kiểm tra thông tin khách hàng
            validateCustomerInfo(bookingRequest, currentCustomer);



            // Kiểm tra thông tin khách hàng
            validateCustomerInfo(bookingRequest, currentCustomer);


            // Tạo booking mới
            Booking booking = new Booking();
            booking.setBookingId(generateBookingID());  // Tạo BookingID
            booking.setBookingStatus(BookingStatus.UNCHECKED);  // Trạng thái booking chưa check-in
            booking.setNumberOfAttendances(requestedSeats);
            booking.setCreatedDate(LocalDateTime.now());
            booking.setHasVisa(bookingRequest.isHasVisa());
            booking.setRequestStatus(RequestStatus.NULL);
            booking.setExpired(false);
            booking.setSeatBooked(requestedSeats);
            booking.setCustomer(currentCustomer);
            booking.setTour(tour);

            // Tính tổng giá tour
            float totalPrice = calculateTotalPrice(tour, bookingRequest.getNumberOfAttendances());
            booking.setTotalPrice(totalPrice);

            // Lưu booking
            bookingRepository.save(booking);

            // Cập nhật số ghế còn lại của tour
            int updatedRemainSeats = tour.getRemainSeat() - requestedSeats;
            tour.setRemainSeat(updatedRemainSeats);
            tourRepository.save(tour);

            // Lập lịch kiểm tra trạng thái thanh toán
            scheduleBookingCancellation(booking.getBookingId());


            BookingAvailableResponse response = modelMapper.map(booking, BookingAvailableResponse.class);
            response.setPaymentStatus(PaymentStatus.PENDING);
            // Trả về response của booking
            return response;
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException(e.getMessage());
        }
    }


    //hàm để check lỗi username phải giống lúc login
    private void validateCustomerInfo(BookingAvailableRequest bookingRequest, Account currentCustomer) {
        CustomerOfBookingResponse customerInfo = bookingRequest.getCustomer();

        // Kiểm tra thông tin khách hàng
        if (customerInfo == null || customerInfo.getFullName() == null || customerInfo.getFullName().isEmpty()) {
            throw new IllegalArgumentException("Customer full name is required!");
        }

        // Kiểm tra và cập nhật fullName
        if (currentCustomer.getFullName() == null || !currentCustomer.getFullName().equals(customerInfo.getFullName())) {
            currentCustomer.setFullName(customerInfo.getFullName());
            accountRepository.save(currentCustomer);
        }

        // Kiểm tra số điện thoại
        if (!currentCustomer.getPhone().equals(customerInfo.getPhone())) {
            throw new ActionException("Logged-in user's phone number does not match the provided phone number!");
        }
    }



    //kiểm tra thanh toán
    public boolean isPaymentCompleted(String bookingID) {
        Booking booking = (Booking) bookingRepository.findByBookingId(bookingID)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        // Lấy danh sách payment liên quan đến booking
        Payment payment = booking.getPayment();
        if (payment == null || payment.getTransactions() == null || payment.getTransactions().isEmpty()) {
            return false; // Nếu không có giao dịch nào, nghĩa là chưa thanh toán
        }

        // Kiểm tra tất cả các giao dịch
        for (Transactions transaction : payment.getTransactions()) {
            if (transaction.getStatus() == TransactionsEnum.SUCCESS) {
                payment.setStatus(PaymentStatus.COMPLETED);
                return true; // Nếu có giao dịch nào thành công, nghĩa là đã thanh toán
            }
        }
        return false; // Nếu không có giao dịch nào thành công, nghĩa là chưa thanh toán
    }


    private void scheduleBookingCancellation(String bookingID) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            try {
                // Kiểm tra trạng thái thanh toán dựa trên transaction
                if (!isPaymentCompleted(bookingID)) {
                    Booking booking = (Booking) bookingRepository.findByBookingId(bookingID)
                            .orElseThrow(() -> new NotFoundException("Booking not found"));

                    // Kiểm tra nếu trạng thái thanh toán vẫn là UNPAID
                    if (booking.getBookingStatus() == BookingStatus.UNCHECKED) {
                        // Hủy booking
                        booking.setBookingStatus(BookingStatus.CANCELLED);
                        booking.setExpired(false);
                        bookingRepository.save(booking);  // Lưu lại trạng thái hủy của booking

                        // Lấy thông tin tour để cập nhật lại số ghế
                        Tour tour = booking.getTour();
                        if (tour != null) {
                            // Cập nhật lại số ghế còn lại
                            int updatedRemainSeats = tour.getRemainSeat() + booking.getNumberOfAttendances();
                            tour.setRemainSeat(updatedRemainSeats);
                            // Lưu cập nhật tour
                            tourRepository.save(tour);
                        }
                    }
                } else {
                    System.out.println("Payment has already been completed, no cancellation.");
                }
            } catch (Exception e) {
                throw new NotFoundException("Error checking");  // Log lỗi nếu có
            } finally {
                // Đảm bảo Thread Pool được tắt sau khi thực hiện xong nhiệm vụ
                scheduler.shutdown();
            }
        }, 3, TimeUnit.HOURS); // Hủy booking sau 24 giờ nếu chưa thanh toán
    }


    // Tự động generateBookingID bằng UUID
    public String generateBookingID() {
        return UUID.randomUUID().toString();  // Sinh chuỗi UUID duy nhất
    }

    private float calculateTotalPrice(Tour tour, int numberOfPersons) {
        // Implement logic for calculating total price
        return (float) (tour.getPrice() * numberOfPersons);
    }

    // tạo transaction
    @Transactional
    public void createTransaction(String bookingID) {
        // Tìm cái Booking
        Booking booking = (Booking) bookingRepository.findByBookingId(bookingID)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        // Kiểm tra nếu BookingStatus là CANCELLED
        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            // Nếu trạng thái booking là CANCELLED, đặt PaymentStatus là CANCELLED
            Payment payment = booking.getPayment();
            if (payment == null) {
                payment = new Payment(); // Tạo payment mới nếu chưa có
                payment.setPaymentId(generatePaymentId()); // Gán paymentId mới
                payment.setBooking(booking);
            }

            // Đặt trạng thái Payment là CANCELLED
            payment.setStatus(PaymentStatus.CANCELLED);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setMethod(PaymentMethod.BANKING);
            payment.setPaymentType(PaymentType.TOUR);
            payment.setDescription("Payment for cancelled booking");
            payment.setCurrency(PaymentCurrency.VND);

            // Lưu Payment
            paymentRepository.save(payment);

            throw new ActionException("Booking has been cancelled, payment is also cancelled.");
        }

        // Tạo Payment cho các booking không bị hủy, trạng thái ban đầu là PENDING
        Payment payment = new Payment();
        payment.setPaymentId(generatePaymentId()); // Gán paymentId mới
        payment.setBooking(booking);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setMethod(PaymentMethod.BANKING);
        payment.setStatus(PaymentStatus.PENDING); // Trạng thái ban đầu là PENDING
        payment.setPaymentType(PaymentType.TOUR);
        payment.setDescription("Thanh toán Tour có sẵn!!");
        payment.setCurrency(PaymentCurrency.VND);

        Set<Transactions> setTransactions = new HashSet<>();

        // Tạo các Transactions cho giao dịch
        // 1. Từ VNPAY -> CUSTOMER
        Account customer = authenticationService.getCurrentAccount();
        Transactions transaction1 = new Transactions();
        transaction1.setFromAccount(null);
        transaction1.setToAccount(customer);
        transaction1.setPayment(payment);
        transaction1.setStatus(TransactionsEnum.SUCCESS);
        transaction1.setDescription("VNPAY TO CUSTOMER");
        setTransactions.add(transaction1);

        // 2. Từ CUSTOMER -> ADMIN
        Account admin = accountRepository.findAccountByRole(Role.ADMIN);
        Transactions transaction2 = new Transactions();
        transaction2.setFromAccount(customer);
        transaction2.setToAccount(admin);
        transaction2.setPayment(payment);
        transaction2.setStatus(TransactionsEnum.SUCCESS);
        transaction2.setDescription("CUSTOMER TO ADMIN");

        // Cập nhật số dư cho ADMIN
        float newBalance = (float) (admin.getBalance() + booking.getTotalPrice());
        admin.setBalance(newBalance);
        setTransactions.add(transaction2);

        // Lưu thông tin vào Payment và Booking
        payment.setTransactions(setTransactions);
        payment.setStatus(PaymentStatus.COMPLETED); // Sau khi các giao dịch thành công
        booking.setBookingStatus(BookingStatus.NOT_CONFIRMED);
        booking.setExpired(true);


        // Lưu các đối tượng
        paymentRepository.save(payment);
        bookingRepository.save(booking);
        accountRepository.save(admin); // Cần phải lưu ADMIN sau khi cập nhật số dư

        // Gửi email xác nhận
        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setSubject("Xác Nhận Đơn Đặt Tour");
        emailDetail.setBooking(booking);
        emailDetail.setAccount(customer);
        emailDetail.setLink("https://yourbookinglink.com"); // Thay thế bằng link phù hợp để quản lý đặt chỗ
        emailService.sendBookingCompleteEmail(emailDetail);
    }

    // Phương thức tạo ID thanh toán hợp lệ với mẫu "PM" + số ngẫu nhiên
    private String generatePaymentId() {
    // Tạo chuỗi ID bắt đầu bằng "PM" và theo sau là một chuỗi số ngẫu nhiên
    return "PM" + (int) (Math.random() * 1000000); // Tạo một số ngẫu nhiên tối đa 6 chữ số
    }



    // update thông tin ticket khi khách hàng hướng đến sân bay
    @Transactional
    public BookingAvailableResponse confirm (String bookingID, String userID) {
        Booking booking = (Booking) bookingRepository.findByBookingId(bookingID)
                .orElseThrow(() -> new NotFoundException("Booking not found"));


        Account consulting = accountRepository.findAccountByUserId(userID);
        if (consulting == null || !consulting.getRole().equals(Role.CONSULTING)) {
            throw new NotFoundException("You are not consulting!!!");
        }
        Account customer = booking.getCustomer();
        if (customer == null) {
            throw new NotFoundException("Customer not found for this booking!");
        }

        booking.setLastUpdate(new Date());
        booking.setConsulting(consulting);
        booking.setBookingStatus(BookingStatus.CHECKED);
        bookingRepository.save(booking);
        return modelMapper.map(booking, BookingAvailableResponse.class);
    }


    // lấy danh sách booking dựa vào mã userID
    @Transactional
    public List<BookingAvailableResponse> getAllBookings(String userID) {
        // Kiểm tra xem tài khoản có tồn tại hay không
        Account account = accountRepository.findAccountByUserId(userID);
        if (account == null) {
            throw new NotFoundException("Account not found");
        }

        // Tìm tất cả các booking của customer bằng tài khoản
        List<Booking> bookings = bookingRepository.findBookingsByCustomer(account);



        // Kiểm tra nếu không có booking nào
        if (bookings.isEmpty()) {
            throw new NotFoundException("No bookings found for this account");
        }

        // Chuyển đổi danh sách booking thành danh sách BookingResponse sử dụng ModelMapper
        return bookings.stream()
                .map(booking -> {
                    BookingAvailableResponse response = modelMapper.map(booking, BookingAvailableResponse.class);
                    // Cập nhật paymentStatus tương ứng với trạng thái booking
                    if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
                        response.setPaymentStatus(PaymentStatus.CANCELLED); // Cập nhật thành CANCELLED nếu booking đã hủy
                    } else if (booking.getPayment() != null) {
                        response.setPaymentStatus(booking.getPayment().getStatus()); // Lấy paymentStatus từ payment
                    } else {
                        response.setPaymentStatus(PaymentStatus.PENDING); // Hoặc có thể đặt giá trị mặc định nào đó
                    }
                    return response;
                })
                .collect(Collectors.toList());
    }




    // lấy danh sách booking IsExprie
    @Transactional
    public List<BookingAvailableResponse> getExpiredBookings(String tourId) {
        // Tìm tất cả các booking có isExpired = true
        List<Booking> expiredBookings = bookingRepository.findBookingsByIsExpiredAndTour_TourId(true, tourId);

        // Kiểm tra nếu không có booking nào
        if (expiredBookings.isEmpty()) {
            throw new NotFoundException("No expired bookings found");
        }

        // Chuyển đổi danh sách booking thành danh sách BookingResponse sử dụng ModelMapper
        return expiredBookings.stream()
                .map(booking ->{
                    BookingAvailableResponse response = modelMapper.map(booking, BookingAvailableResponse.class);
                    // Cập nhật paymentStatus tương ứng với trạng thái booking
                    if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
                        response.setPaymentStatus(PaymentStatus.CANCELLED); // Cập nhật thành CANCELLED nếu booking đã hủy
                    } else if (booking.getPayment() != null) {
                        response.setPaymentStatus(booking.getPayment().getStatus()); // Lấy paymentStatus từ payment
                    } else {
                        response.setPaymentStatus(PaymentStatus.PENDING); // Hoặc có thể đặt giá trị mặc định nào đó
                    }
                    return response;
                })
                .collect(Collectors.toList());
    }



    // thanh toan bang tien mat
    @Transactional
    public BookingAvailableResponse createTicketCast(BookingAvailableRequest bookingAvailableRequest) {
        try {
            Tour tour = tourRepository.findTourByTourId(bookingAvailableRequest.getTourID());
            if (tour == null) {
                throw new NotFoundException("Tour Not Found!");
            }

            // Kiểm tra xem khách hàng có tồn tại và có vai trò "CUSTOMER" hay không
            Account customer = authenticationService.getCurrentAccount();
            if (customer == null || !customer.getRole().equals(Role.CUSTOMER)) {
                throw new NotFoundException("CUSTOMER Not Found!");
            }


            // Kiểm tra số ghế còn lại của tour
            int requestedSeats = bookingAvailableRequest.getNumberOfAttendances(); // số ghế hành khách đặt = hành khách đi
            if (tour.getRemainSeat() < requestedSeats) {
                throw new ActionException("Not enough seats available!");
            }

            Booking booking = new Booking();
            booking.setBookingId(generateBookingID());
            booking.setBookingStatus(BookingStatus.NOT_CONFIRMED); // chưa check tại sân bay
            booking.setNumberOfAttendances(requestedSeats);
            booking.setRequestStatus(RequestStatus.NULL);
            booking.setExpired(true);
            booking.setCreatedDate(LocalDateTime.now());
            booking.setSeatBooked(requestedSeats);

            float totalPrice = calculateTotalPrice(tour, bookingAvailableRequest.getNumberOfAttendances());
            booking.setTotalPrice(totalPrice);


            booking.setCustomer(customer);
            booking.setTour(tour);
            bookingRepository.save(booking);

            // Cập nhật số ghế còn lại của tour
            int updatedRemainSeats = tour.getRemainSeat() - requestedSeats;
            tour.setRemainSeat(updatedRemainSeats);
            tourRepository.save(tour);

            validateCustomerInfo(bookingAvailableRequest, customer);

            // Tạo payment
            Payment payment = new Payment();
            payment.setBooking(booking);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setMethod(PaymentMethod.CASH);
            payment.setPaymentId(generatePaymentId()); // Gán paymentId mới
            payment.setStatus(PaymentStatus.COMPLETED); // Trạng thái ban đầu là PENDING
            payment.setPaymentType(PaymentType.TOUR);
            payment.setCurrency(PaymentCurrency.VND);
            payment.setDescription("Pay by Cash");
            payment.setCurrency(PaymentCurrency.VND);
            paymentRepository.save(payment);

            EmailDetail emailDetail = new EmailDetail();
            emailDetail.setSubject("Xác Nhận Đơn Đặt Tour");
            emailDetail.setBooking(booking);
            emailDetail.setAccount(customer);
            emailDetail.setLink("https://google.com"); // Thay thế bằng link phù hợp để quản lý đặt chỗ
            emailService.sendBookingCompleteEmail(emailDetail);

            BookingAvailableResponse response = modelMapper.map(booking, BookingAvailableResponse.class);
            response.setPaymentStatus(PaymentStatus.COMPLETED);

            return response;
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException(e.getMessage());
        }
    }
}


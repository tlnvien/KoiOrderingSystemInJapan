package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.*;
import com.project.KoiBookingSystem.enums.*;
import com.project.KoiBookingSystem.exception.ActionException;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.model.request.BookingRequest;
import com.project.KoiBookingSystem.model.response.BookingResponse;
import com.project.KoiBookingSystem.model.response.EmailDetail;
import com.project.KoiBookingSystem.repository.*;
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
public class BookingService {

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
    public BookingResponse createTicket(BookingRequest bookingRequest) {
        try {
            Tour tour = tourRepository.findTourByTourID(bookingRequest.getTourID());
            if (tour == null) {
                throw new NotFoundException("Tour Not Found!");
            }

            // Kiểm tra xem khách hàng có tồn tại và có vai trò "CUSTOMER" hay không
            Account customer = authenticationService.getCurrentAccount();
            if (customer == null || !customer.getRole().equals(Role.CUSTOMER)) {
                throw new NotFoundException("CUSTOMER Not Found!");
            }


            // Kiểm tra số ghế còn lại của tour
            int requestedSeats = bookingRequest.getNumberOfPerson(); // số ghế hành khách đặt = hành khách đi
            if (tour.getRemainSeat() < requestedSeats) {
                throw new ActionException("Not enough seats available!");
            }

            Booking booking = new Booking();
            booking.setBookingID(generateBookingID());
            booking.setBookingStatus(BookingStatus.NULL); // chưa check tại sân bay
            booking.setBookingType(BookingType.AVAILABLE); // xác định phương thức booking
            booking.setNumberOfPerson(requestedSeats);
            booking.setCreateDate(LocalDateTime.now());
            booking.setSeatBooked(requestedSeats);


            float totalPrice = calculateTotalPrice(tour, bookingRequest.getNumberOfPerson());
            booking.setTotalPrice(totalPrice);

            booking.setPaymentStatus(PaymentStatus.UNPAID);
            booking.setCustomer(customer);
            booking.setTourId(tour);
            bookingRepository.save(booking);

            // Cập nhật số ghế còn lại của tour
            int updatedRemainSeats = tour.getRemainSeat() - requestedSeats;
            tour.setRemainSeat(updatedRemainSeats);
            tourRepository.save(tour);

            // Lập lịch kiểm tra trạng thái thanh toán sau 5 giây
            scheduleBookingCancellation(booking.getBookingID());

            return modelMapper.map(booking, BookingResponse.class);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException(e.getMessage());
        }
    }

    public boolean isPaymentCompleted(String bookingID) {
        Booking booking = bookingRepository.findByBookingID(bookingID)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        // Lấy danh sách payment liên quan đến booking
        Payment payment = booking.getPayment();
        if (payment == null || payment.getTransactions() == null || payment.getTransactions().isEmpty()) {
            return false; // Nếu không có giao dịch nào, nghĩa là chưa thanh toán
        }

        // Kiểm tra tất cả các giao dịch
        for (Transactions transaction : payment.getTransactions()) {
            if (transaction.getStatus() == TransactionEnums.SUCCESS) {
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
                    Booking booking = bookingRepository.findByBookingID(bookingID)
                            .orElseThrow(() -> new NotFoundException("Booking not found"));

                    // Kiểm tra nếu trạng thái thanh toán vẫn là UNPAID
                    if (booking.getPaymentStatus() == PaymentStatus.UNPAID) {
                        // Hủy booking
                        booking.setBookingStatus(BookingStatus.CANCELLED);
                        booking.setExpired(false);
                        bookingRepository.save(booking);  // Lưu lại trạng thái hủy của booking

                        // Lấy thông tin tour để cập nhật lại số ghế
                        Tour tour = booking.getTourId();
                        if (tour != null) {
                            // Cập nhật lại số ghế còn lại
                            int updatedRemainSeats = tour.getRemainSeat() + booking.getNumberOfPerson();
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
        return tour.getPrice() * numberOfPersons;
    }

    // tạo transaction
    @Transactional
    public void createTransaction(String bookingID) {
        //tìm cái Booking

        Booking booking = bookingRepository.findByBookingID(bookingID)
                .orElseThrow(() -> new NotFoundException("Booking not found"));


        //tạo payment
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setCreatedAt(new Date());
        payment.setPayment_method(PaymentEnums.BANKING);


        Set<Transactions> setTransactions = new HashSet<>();


        // tạo transactions
        //1. từ VNPAY -> CUSTOMER
        Transactions transactions1 = new Transactions();
        Account customer = authenticationService.getCurrentAccount();
        transactions1.setFrom(null);
        transactions1.setTo(customer);
        transactions1.setPayment(payment);
        transactions1.setStatus(TransactionEnums.SUCCESS);
        transactions1.setDescription(" VNPAY TO CUSTOMER");
        setTransactions.add(transactions1);

        // tạo transactions
        //2. từ CUSTOMER -> ADMIN
        Transactions transactions2 = new Transactions();
        Account admin = accountRepository.findAccountByRole(Role.ADMIN);
        transactions2.setFrom(customer);
        transactions2.setTo(admin);
        transactions2.setPayment(payment);
        transactions2.setStatus(TransactionEnums.SUCCESS);
        transactions2.setDescription(" CUSTOMER TO ADMIN");

        float newBalance = admin.getBalance() + booking.getTotalPrice();
        admin.setBalance(newBalance);
        setTransactions.add(transactions2);


        payment.setTransactions(setTransactions);
        booking.setExpired(true);
        booking.setPaymentStatus(PaymentStatus.COMPLETED);
        bookingRepository.save(booking);
        accountRepository.save(customer);
        accountRepository.save(admin);
        paymentRepository.save(payment);

        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setSubject("Xác Nhận Đơn Đặt Tour");
        emailDetail.setBooking(booking);
        emailDetail.setAccount(customer);
        emailDetail.setLink("https://google.com"); // Thay thế bằng link phù hợp để quản lý đặt chỗ
        emailService.sendBookingCompleteEmail(emailDetail);
    }



    // update thông tin ticket khi khách hàng hướng đến sân bay
    @Transactional
    public BookingResponse confirm (String bookingID, String userID)
    {
        Booking booking = bookingRepository.findByBookingID(bookingID)
                .orElseThrow(() -> new NotFoundException("Booking not found"));


        Account consulting = accountRepository.findAccountByUserID(userID);
        if (consulting == null || !consulting.getRole().equals(Role.CONSULTING)) {
            throw new NotFoundException("You are not consulting!!!");
        }

        booking.setLastUpdate(new Date());
        booking.setConsulting(consulting);
        booking.setBookingStatus(BookingStatus.CHECKED);
        bookingRepository.save(booking);
        return modelMapper.map(booking, BookingResponse.class);
    }


    // lấy danh sách booking dựa vào mã userID (Customer)
    @Transactional
    public List<BookingResponse> getAllBookings(String userID) {
        // Kiểm tra xem tài khoản có tồn tại hay không
       Account account = accountRepository.findAccountByUserID(userID);
       if (account == null) {
           throw new NotFoundException("Account not found");
       }

        // Tìm tất cả các booking của customer bằng tài khoản
        List<Booking> bookings = bookingRepository.findBookingsByCustomer(account);

        // Chuyển đổi danh sách booking thành danh sách BookingResponse sử dụng ModelMapper
        return bookings.stream()
                .map(booking -> modelMapper.map(booking, BookingResponse.class))
                .collect(Collectors.toList());
    }

    // thanh toan bang tien mat
    @Transactional
    public BookingResponse createTicketCast(BookingRequest bookingRequest) {
        try {
            Tour tour = tourRepository.findTourByTourID(bookingRequest.getTourID());
            if (tour == null) {
                throw new NotFoundException("Tour Not Found!");
            }

            // Kiểm tra xem khách hàng có tồn tại và có vai trò "CUSTOMER" hay không
            Account customer = authenticationService.getCurrentAccount();
            if (customer == null || !customer.getRole().equals(Role.CUSTOMER)) {
                throw new NotFoundException("CUSTOMER Not Found!");
            }


            // Kiểm tra số ghế còn lại của tour
            int requestedSeats = bookingRequest.getNumberOfPerson(); // số ghế hành khách đặt = hành khách đi
            if (tour.getRemainSeat() < requestedSeats) {
                throw new ActionException("Not enough seats available!");
            }

            Booking booking = new Booking();
            booking.setBookingID(generateBookingID());
            booking.setPaymentStatus(PaymentStatus.COMPLETED);
            booking.setBookingStatus(BookingStatus.NULL); // chưa check tại sân bay
            booking.setBookingType(BookingType.AVAILABLE); // xác định phương thức booking
            booking.setNumberOfPerson(requestedSeats);
            booking.setCreateDate(LocalDateTime.now());
            booking.setSeatBooked(requestedSeats);

            float totalPrice = calculateTotalPrice(tour, bookingRequest.getNumberOfPerson());
            booking.setTotalPrice(totalPrice);


            booking.setCustomer(customer);
            booking.setTourId(tour);
            bookingRepository.save(booking);

            // Cập nhật số ghế còn lại của tour
            int updatedRemainSeats = tour.getRemainSeat() - requestedSeats;
            tour.setRemainSeat(updatedRemainSeats);
            tourRepository.save(tour);


            //tạo payment
            Payment payment = new Payment();
            payment.setBooking(booking);
            payment.setCreatedAt(new Date());
            payment.setPayment_method(PaymentEnums.CAST);
            paymentRepository.save(payment);

            EmailDetail emailDetail = new EmailDetail();
            emailDetail.setSubject("Xác Nhận Đơn Đặt Tour");
            emailDetail.setBooking(booking);
            emailDetail.setAccount(customer);
            emailDetail.setLink("https://google.com"); // Thay thế bằng link phù hợp để quản lý đặt chỗ
            emailService.sendBookingCompleteEmail(emailDetail);

            return modelMapper.map(booking, BookingResponse.class);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException(e.getMessage());
        }
    }
}





package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.*;
import com.project.KoiBookingSystem.enums.*;
import com.project.KoiBookingSystem.exception.ActionException;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.model.request.BookingRequest;
import com.project.KoiBookingSystem.model.response.BookingResponse;
import com.project.KoiBookingSystem.model.response.EmailDetail;
import com.project.KoiBookingSystem.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.*;
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
    OrderRepository orderRepository;
    @Autowired
    private PaymentRepository paymentRepository;


    @Autowired
    EmailService emailService;

    public BookingResponse createTicket(BookingRequest bookingRequest) {
        try {
            Tour tour = tourRepository.findTourByTourID(bookingRequest.getTourID());
            if (tour == null) {
                throw new NotFoundException("Tour Not Found!");
            }

            // Kiểm tra xem khách hàng có tồn tại và có vai trò "CUSTOMER" hay không
            Account customer = accountRepository.findAccountByUserID(bookingRequest.getCustomerID());
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
            booking.setBookingStatus(BookingStatus.UNCHECKED); // chưa check tại sân bay
            booking.setBookingType(BookingType.AVAILABLE); // xác định phương thức booking
            booking.setNumberOfPerson(requestedSeats);
            booking.setCreateDate(bookingRequest.getCreateDate());
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


            return modelMapper.map(booking, BookingResponse.class);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException(e.getMessage());
        }
    }

    public String generateBookingID() {
        Booking lastBooking = bookingRepository.findTopByOrderByIdDesc();
        int lastId = 0;
        if (lastBooking != null && lastBooking.getBookingID() != null) {
            String lastBookingId = lastBooking.getBookingID();
            lastId = Integer.parseInt(lastBookingId.substring(1));
        }

        return "B" + (lastId + 1);
    }


    private float calculateTotalPrice(Tour tour, int numberOfPersons) {
        // Implement logic for calculating total price
        return tour.getPrice() * numberOfPersons;
    }

    // tạo transaction

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
        Account customer = accountRepository.findAccountByUserID(booking.getCustomer().getUserID());
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
    public BookingResponse confirm (String bookingID, String userID) {
            Booking booking = bookingRepository.findByBookingID(bookingID)
                    .orElseThrow(() -> new NotFoundException("Booking not found"));
            Account consulting = accountRepository.findAccountByUserID(userID);
            booking.setLastUpdate(new Date());
            booking.setConsulting(consulting);
            booking.setBookingStatus(BookingStatus.CHECKED);
            bookingRepository.save(booking);
            return modelMapper.map(booking, BookingResponse.class);
    }

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
}



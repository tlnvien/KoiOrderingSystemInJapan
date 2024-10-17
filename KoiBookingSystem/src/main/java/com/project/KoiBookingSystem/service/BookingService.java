package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.*;
import com.project.KoiBookingSystem.enums.*;
import com.project.KoiBookingSystem.exception.ActionException;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.exception.PaymentException;
import com.project.KoiBookingSystem.model.request.BookingRequest;
import com.project.KoiBookingSystem.model.response.BookingResponse;
import com.project.KoiBookingSystem.model.response.EmailDetail;
import com.project.KoiBookingSystem.repository.AccountRepository;
import com.project.KoiBookingSystem.repository.BookingRepository;
import com.project.KoiBookingSystem.repository.PaymentRepository;
import com.project.KoiBookingSystem.repository.TourRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class BookingService {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TourRepository tourRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    VNPayService vnPayService;

    @Autowired
    PaymentService paymentService;

    @Autowired
    EmailService emailService;

    @Transactional
    public BookingResponse createNewAvailableBooking(BookingRequest bookingRequest, String tourId) {
        Account customer = getAccount(bookingRequest);
        Tour tour = getAvailableTour(tourId, bookingRequest.getNumberOfAttendees());

        Booking booking = createBookingDefault(customer, tour, bookingRequest);
        booking.setSales(null);
        booking.setTotalPrice(tour.getPrice() * bookingRequest.getNumberOfAttendees());
        booking.setRequestStatus(RequestStatus.NULL);
        
        Booking newBooking = bookingRepository.save(booking);
        tour.setRemainSeat(tour.getRemainSeat() - newBooking.getNumberOfAttendances());
        tourRepository.save(tour);
        return convertToBookingResponse(newBooking);
    }

    @Transactional
    public BookingResponse createNewRequestedBooking(BookingRequest bookingRequest) {
        Account customer = getCustomer();

        Booking booking = createBookingDefault(customer, null, bookingRequest);
        booking.setSales(null);
        booking.setRequestStatus(RequestStatus.NOT_TAKEN);
        booking.setTotalPrice(0);

        Booking newBooking = bookingRepository.save(booking);
        return convertToBookingResponse(newBooking);
    }

    public BookingResponse checkInBooking(String bookingId, BookingStatus status) {
        Account consulting = authenticationService.getCurrentAccount();
        if (consulting == null || !consulting.getRole().equals(Role.CONSULTING)) {
            throw new ActionException("Invalid activity! Only consulting staff can check in!");
        }
        UUID uuid = UUID.fromString(bookingId);
        Booking booking = bookingRepository.findBookingByBookingId(uuid);
        validateBooking(booking);
        if (!booking.getBookingStatus().equals(BookingStatus.UNCHECKED)) {
            throw new ActionException("Booking can not be checked in!");
        }
        booking.setBookingStatus(status);
        Booking checkedBooking = bookingRepository.save(booking);
        return convertToBookingResponse(checkedBooking);
    }

    // nhận yêu cầu đặt tour từ phía khách hàng
    @Transactional
    public BookingResponse takeRequestBooking(String bookingId) {
        Account sales = getSales();

        UUID uuid = UUID.fromString(bookingId);
        Booking booking = bookingRepository.findBookingByBookingId(uuid);

        validateBooking(booking);
        if (booking.getTour() != null) {
            throw new ActionException("This booking can not be taken to support by Sales staff!");
        }
        if (!booking.getRequestStatus().equals(RequestStatus.NOT_TAKEN)) {
            throw new ActionException("This booking can not be taken to support!");
        }
        booking.setRequestStatus(RequestStatus.IN_PROGRESS);
        booking.setSales(sales);
        Booking takenBooking = bookingRepository.save(booking);
        return convertToBookingResponse(takenBooking);
    }

    // LIÊN KẾT BOOKING ĐÃ YÊU CẦU VỚI TOUR ĐƯỢC TẠO BỞI THẰNG NHÂN VIÊN KINH DOANH
    @Transactional
    public BookingResponse associateBookingToRequestedTour(String bookingId, String tourId) {
        Account sales = getSales();

        UUID uuid = UUID.fromString(bookingId);
        Booking booking = bookingRepository.findBookingByBookingId(uuid);

        validateBooking(booking);
        Tour tour = getRequestTour(tourId);
        booking.setTour(tour);
        booking.setTotalPrice(tour.getPrice());
        Booking associatedBooking = bookingRepository.save(booking);

        return convertToBookingResponse(associatedBooking);
    }


    private Account getAccount(BookingRequest bookingRequest) {
        Account customer = getCustomer();
        updateCustomerDetails(customer, bookingRequest);
        accountRepository.save(customer);
        return customer;
    }

    @Transactional
    public void createBookingTransaction(String bookingId) {
        UUID uuid = UUID.fromString(bookingId);
        Booking booking = bookingRepository.findBookingByBookingId(uuid);
        validateBooking(booking);
        if (booking.getTour() == null) {
            throw new ActionException("Booking can not be paid yet!");
        }
        try {
            Payment payment = createBookingPayment(booking);

            Set<Transactions> transactionsSet = createBookingTransactions(payment);

            payment.setTransactions(transactionsSet);
            payment.setStatus(PaymentStatus.COMPLETED);

            paymentRepository.save(payment);

            sendBookingConfirmation(booking.getCustomer(), booking);
        } catch (Exception e) {
            Tour tour = booking.getTour();
            if (tour.getType().equals(TourType.AVAILABLE_TOUR)) {
                tour.setRemainSeat(tour.getRemainSeat() + booking.getNumberOfAttendances());
                tourRepository.save(tour);
            }
            throw new PaymentException("Payment transaction failed: " + e.getMessage());
        }
    }

    public String createBookingPaymentUrl(String bookingId) {
        UUID uuid = UUID.fromString(bookingId);
        Booking booking = bookingRepository.findBookingByBookingId(uuid);
        if (booking == null) {
            throw new NotFoundException("Booking Not Found!");
        }
        double amount = booking.getTotalPrice();
        try {
            return vnPayService.createPaymentUrl(bookingId, amount, "Booking");
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new PaymentException("Payment error: " + e.getMessage());
        }
    }

    public BookingResponse getBookingById(String bookingId) {
        UUID uuid = UUID.fromString(bookingId);
        Booking booking = bookingRepository.findBookingByBookingId(uuid);
        if (booking == null) throw new NotFoundException("Booking Not Found!");
        return convertToBookingResponse(booking);
    }

    public BookingResponse convertToBookingResponse(Booking booking) {
        BookingResponse bookingResponse = new BookingResponse();

        bookingResponse.setBookingId(booking.getBookingId().toString());

        if (booking.getPayment() == null) {
            bookingResponse.setPaymentId("Not Paid Yet!");
        } else {
            bookingResponse.setPaymentId(booking.getPayment().getPaymentId());
        }
        bookingResponse.setTourId(booking.getTour().getTourId());
        bookingResponse.setCustomerId(booking.getCustomer().getUserId());
        bookingResponse.setCreatedDate(booking.getCreatedDate());
        bookingResponse.setNumberOfAttendances(booking.getNumberOfAttendances());
        bookingResponse.setHasVisa(booking.isHasVisa());
        bookingResponse.setTotalPrice(booking.getTotalPrice());
        bookingResponse.setStatus(booking.getBookingStatus());

        return bookingResponse;
    }

    @Scheduled(fixedRate = 3600000)
    public void expireBooking() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findAll();

        for (Booking booking : bookings) {
            if (booking.getTour() != null && booking.getPayment() == null && booking.getCreatedDate().plusHours(3).isBefore(now)) {
                booking.setExpired(true);
                booking.setBookingStatus(BookingStatus.CANCELLED);
                bookingRepository.save(booking);
                if (booking.getTour().getType().equals(TourType.REQUESTED_TOUR) && booking.getTour().getStatus() != TourStatus.CANCELLED) {
                    booking.getTour().setStatus(TourStatus.CANCELLED);
                    tourRepository.save(booking.getTour());
                }
            }
        }
    }

    private Transactions createTransaction(Account fromAccount, Account toAccount, Payment payment, String description) {
        Transactions transaction = new Transactions();
        try {
            transaction.setFromAccount(fromAccount);
            transaction.setToAccount(toAccount);
            transaction.setPayment(payment);
            transaction.setDescription(description);
            transaction.setStatus(TransactionsEnum.SUCCESS);

            return transaction;
        } catch (Exception e) {
            transaction.setStatus(TransactionsEnum.FAILED);
            throw new ActionException("Transaction initiation failed!");
        }
    }

    private Payment createBookingPayment(Booking booking) {
        Payment payment = new Payment();
        payment.setPaymentId(paymentService.generatePaymentId());
        payment.setBooking(booking);
        payment.setOrders(null);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setDescription("Thanh toan bang VNPAY: " + booking.getBookingId());
        payment.setPaymentType(PaymentType.TOUR);
        payment.setMethod(PaymentMethod.BANKING);
        payment.setTour(booking.getTour());

        return payment;
    }

    private void validateBooking(Booking booking) {
        if (booking == null) {
            throw new NotFoundException("Booking Not Found!");
        }
        if (booking.isExpired()) {
            throw new ActionException("Booking has expired and can not be processed!");
        }
    }

    private Set<Transactions> createBookingTransactions(Payment payment) {
        Set<Transactions> transactionsSet = new HashSet<>();

        Account customer = getCustomer();
        transactionsSet.add(createTransaction(null, customer, payment, "Transaction 1: VNPay To Customer"));

        Account admin = getAdminAccount();
        transactionsSet.add(createTransaction(customer, admin, payment, "Transaction 2: Customer To Admin"));

        double newBalance = admin.getBalance() + payment.getBooking().getTotalPrice();
        admin.setBalance(newBalance);

        accountRepository.save(admin);
        return transactionsSet;
    }

    private Account getCustomer() {
        Account customer = authenticationService.getCurrentAccount();
        if (customer == null || !customer.getRole().equals(Role.CUSTOMER)) {
            throw new ActionException("Invalid Activity! Only customer can process this action!");
        }
        return customer;
    }

    private Account getAdminAccount() {
        Account admin = accountRepository.findAccountByRole(Role.ADMIN);
        if (admin == null) {
            throw new NotFoundException("Admin Account Not Found!");
        }
        return admin;
    }

    private Booking createBookingDefault(Account customer, Tour tour, BookingRequest bookingRequest) {
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setTour(tour);
        booking.setPayment(null);
        booking.setDescription(bookingRequest.getDescription());
        booking.setNumberOfAttendances(bookingRequest.getNumberOfAttendees());
        booking.setHasVisa(bookingRequest.isHasVisa());
        booking.setBookingStatus(BookingStatus.UNCHECKED);
        booking.setCreatedDate(LocalDateTime.now());

        return booking;

    }

    private void updateCustomerDetails(Account customer, BookingRequest bookingRequest) {
        if (customer.getFullName() == null || customer.getFullName().isEmpty()) {
            customer.setFullName(bookingRequest.getFullName());
        }
        if (customer.getPhone() == null || customer.getPhone().isEmpty()) {
            customer.setPhone(bookingRequest.getPhone());
        }
    }

    private Tour getAvailableTour(String tourId, int numberOfAttendees) {
        Tour tour = tourRepository.findTourByTourId(tourId);
        if (tour == null) {
            throw new NotFoundException("Tour Not Found!");
        }
        if (!tour.getType().equals(TourType.AVAILABLE_TOUR)) {
            throw new ActionException("Tour is not available for booking!");
        }
        if (tour.getRemainSeat() < numberOfAttendees) {
            throw new ActionException("Not enough available seat for this tour!");
        }
        return tour;
    }

    private Tour getRequestTour(String tourId) {
        Tour tour = tourRepository.findTourByTourId(tourId);
        if (tour == null || !tour.getType().equals(TourType.REQUESTED_TOUR)) {
            throw new NotFoundException("Tour Not Found!");
        }
        return tour;
    }

    private Account getSales() {
        Account sales = authenticationService.getCurrentAccount();
        if (sales == null || !sales.getRole().equals(Role.SALES)) {
            throw new ActionException("Only Sales staff can take this action!");
        }
        return sales;
    }

    private void sendBookingConfirmation(Account account, Booking booking) {
        EmailDetail emailDetail = createEmailDetail(account, booking, "Xác nhận đơn đặt Tour", "https://google.com/");
        emailService.sendBookingCompleteEmail(emailDetail);
    }

    private EmailDetail createEmailDetail(Account account, Booking booking, String subject, String link) {
        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setAccount(account);
        emailDetail.setBooking(booking);
        emailDetail.setSubject(subject);
        emailDetail.setLink(link);

        return emailDetail;
    }



    // nhận yêu cầu booking của khách hàng
    // yêu cầu booking ban đầu của khách sẽ không có tour
}

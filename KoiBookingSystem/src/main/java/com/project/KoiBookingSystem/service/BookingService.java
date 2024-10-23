package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.*;
import com.project.KoiBookingSystem.enums.*;
import com.project.KoiBookingSystem.exception.*;
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
import java.util.*;
import java.util.stream.Collectors;

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
        Account customer = getAccount(bookingRequest);

        Booking booking = createBookingDefault(customer, null, bookingRequest);
        booking.setSales(null);
        booking.setRequestStatus(RequestStatus.NOT_TAKEN);
        booking.setTotalPrice(0);

        Booking newBooking = bookingRepository.save(booking);
        return convertToBookingResponse(newBooking);
    }

    public List<BookingResponse> getAllRequests() {
        List<Booking> requests = bookingRepository.findBySalesIsNullAndIsExpiredFalse();
        if (requests.isEmpty()) {
            throw new EmptyListException("Request is empty!");
        }
        return requests.stream().map(this::convertToBookingResponse).collect(Collectors.toList());
    }

    public List<BookingResponse> getAllBookingInfoByConsulting() {
        Account consulting = authenticationService.getCurrentAccount();
        if (consulting == null || !consulting.getRole().equals(Role.CONSULTING)) {
            throw new ActionException("Only consulting staff can perform this action!");
        }
        List<Booking> bookings = bookingRepository.findByTour_Consulting_UserId(consulting.getUserId());
        if (bookings.isEmpty()) {
            throw new EmptyListException("You are not in any tour yet!");
        }
        return bookings.stream().map(this::convertToBookingResponse).collect(Collectors.toList());
    }


    // THẰNG CONSULTING CẬP NHẬT TRẠNG THÁI BOOKING Ở SÂN BAY NẾU KHÁCH TỚI
    @Transactional
    public BookingResponse checkInBooking(String bookingId, BookingStatus status) {
        Booking booking = bookingRepository.findBookingByBookingId(bookingId);
        validateBooking(booking);
        Account consulting = authenticationService.getCurrentAccount();
        getConsulting(consulting, booking);
        if (!booking.getBookingStatus().equals(BookingStatus.UNCHECKED)) {
            throw new ActionException("Booking can not be checked in!");
        }
        booking.setBookingStatus(status);
        Booking checkedBooking = bookingRepository.save(booking);
        return convertToBookingResponse(checkedBooking);
    }

    // NHẬN REQUEST TỪ THẰNG SALES
    @Transactional
    public BookingResponse takeRequestBooking(String bookingId) {
        Account sales = getSales();

        Booking booking = bookingRepository.findBookingByBookingId(bookingId);

        validateBooking(booking);
        if (booking.getSales() != null) {
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

        Booking booking = bookingRepository.findBookingByBookingId(bookingId);

        validateBooking(booking);
        if (booking.getSales() == null) {
            throw new ActionException("This booking can not be associated yet as taken sales staff not found!");
        }
        Tour tour = getRequestTour(tourId);
        booking.setTour(tour);
        booking.setTotalPrice(tour.getPrice());
        booking.setRequestStatus(RequestStatus.COMPLETED);
        Booking associatedBooking = bookingRepository.save(booking);
        sendBookingPayment(associatedBooking.getCustomer(), associatedBooking);
        return convertToBookingResponse(associatedBooking);
    }


    private Account getAccount(BookingRequest bookingRequest) {
        Account customer = getCustomer();
        updateCustomerDetails(customer, bookingRequest);
        return customer;
    }

    @Transactional
    public void createBookingTransaction(String bookingId) {
        Booking booking = bookingRepository.findBookingByBookingId(bookingId);
        validateBooking(booking);
        if (booking.getTour() == null || booking.getPayment() != null) {
            throw new ActionException("Booking can not be paid yet!");
        }
        Payment payment = createBookingPayment(booking);
        try {
            Set<Transactions> transactionsSet = createBookingTransactions(payment, booking);

            payment.setTransactions(transactionsSet);
            payment.setStatus(PaymentStatus.COMPLETED);

            paymentRepository.save(payment);

            sendBookingConfirmation(booking.getCustomer(), booking);
        } catch (Exception e) {
            Tour tour = booking.getTour();
            if (tour.getType().equals(TourType.AVAILABLE_TOUR)) {
                tour.setRemainSeat(tour.getRemainSeat() + booking.getNumberOfAttendances());
                tourRepository.save(tour);
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
            }
            throw new PaymentException("Payment transaction failed: " + e.getMessage());
        }
    }

    public String createBookingPaymentUrl(String bookingId) {
        Booking booking = bookingRepository.findBookingByBookingId(bookingId);
        validateBooking(booking);
        double amount = booking.getTotalPrice();
        try {
            return vnPayService.createPaymentUrl(bookingId, amount, "Booking");
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new PaymentException("Payment error: " + e.getMessage());
        }
    }


    public BookingResponse getBookingById(String bookingId) {
        Booking booking = bookingRepository.findBookingByBookingId(bookingId);
        if (booking == null) throw new NotFoundException("Booking Not Found!");
        return convertToBookingResponse(booking);
    }

    public BookingResponse convertToBookingResponse(Booking booking) {
        BookingResponse bookingResponse = new BookingResponse();

        bookingResponse.setBookingId(booking.getBookingId());

        if (booking.getPayment() == null) {
            bookingResponse.setPaymentId("Not Paid Yet!");
        } else {
            bookingResponse.setPaymentId(booking.getPayment().getPaymentId());
        }
        if (booking.getTour() != null) {
            bookingResponse.setTourId(booking.getTour().getTourId());
        }
        bookingResponse.setCustomerName(booking.getCustomer().getFullName());
        bookingResponse.setPhone(booking.getCustomer().getPhone());
        bookingResponse.setCreatedDate(booking.getCreatedDate());
        bookingResponse.setDescription(booking.getDescription());
        bookingResponse.setNumberOfAttendances(booking.getNumberOfAttendances());
        bookingResponse.setHasVisa(booking.isHasVisa());
        bookingResponse.setTotalPrice(booking.getTotalPrice());
        bookingResponse.setStatus(booking.getBookingStatus());

        return bookingResponse;
    }


    // HÀM NÀY DÙNG ĐỂ TÍNH THỜI GIAN HỦY BOOKING NẾU KHÁCH HÀNG KHÔNG THANH TOÁN, NẾU BOOKING THUỘC VỀ MỘT TOUR THEO YÊU CẦU, TOUR ĐÓ SẼ BỊ HỦY THEO
    @Scheduled(fixedRate = 3600000) // 1 HOUR = 3600000
    public void expireBooking() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> availableTourBookings = bookingRepository.findBookingsToExpire(now.minusHours(3), TourType.AVAILABLE_TOUR);

        List<Booking> requestedTourBookings = bookingRepository.findBookingsToExpire(now.minusHours(24), TourType.REQUESTED_TOUR);

        List<Booking> bookingsToUpdate = new ArrayList<>();
        List<Tour> toursToUpdate = new ArrayList<>();

        for (Booking booking : availableTourBookings) {
            booking.setExpired(true);
            booking.setBookingStatus(BookingStatus.CANCELLED);
            bookingsToUpdate.add(booking);

            booking.getTour().setRemainSeat(booking.getTour().getRemainSeat() + booking.getNumberOfAttendances());
            toursToUpdate.add(booking.getTour());
        }

        for (Booking booking : requestedTourBookings) {
            booking.setExpired(true);
            booking.setBookingStatus(BookingStatus.CANCELLED);
            bookingsToUpdate.add(booking);

            if (booking.getTour() != null && !booking.getTour().getStatus().equals(TourStatus.CANCELLED)) {
                booking.getTour().setStatus(TourStatus.CANCELLED);
                toursToUpdate.add(booking.getTour());
            }
        }
        if (!bookingsToUpdate.isEmpty()) {
            bookingRepository.saveAll(bookingsToUpdate);
        }
        if (!toursToUpdate.isEmpty()) {
            tourRepository.saveAll(toursToUpdate);
        }

    }

    private Transactions createTransaction(Account fromAccount, Account toAccount, Payment payment, String description, double amount) {
        Transactions transaction = new Transactions();
        try {
            transaction.setFromAccount(fromAccount);
            transaction.setToAccount(toAccount);
            transaction.setPayment(payment);
            transaction.setDescription(description);
            transaction.setAmount(amount);
            transaction.setStatus(TransactionsEnum.SUCCESS);

            return transaction;
        } catch (Exception e) {
            transaction.setStatus(TransactionsEnum.FAILED);
            throw new PaymentException("Transaction initiation failed!");
        }
    }

    private Payment createBookingPayment(Booking booking) {
        Payment payment = new Payment();
        payment.setPaymentId(paymentService.generatePaymentId());
        payment.setBooking(booking);
        payment.setOrders(null);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setDescription("Thanh toan bang VNPAY: " + booking.getBookingId());
        payment.setPrice(booking.getTotalPrice());
        payment.setPaymentType(PaymentType.TOUR);
        payment.setMethod(PaymentMethod.BANKING);
        payment.setCurrency(PaymentCurrency.VND);

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

    private Set<Transactions> createBookingTransactions(Payment payment, Booking booking) {
        Set<Transactions> transactionsSet = new HashSet<>();

        transactionsSet.add(createTransaction(null, booking.getCustomer(), payment, "Transaction 1: VNPay To Customer", 0));

        Account admin = getAdminAccount();
        double amount = payment.getBooking().getTotalPrice();
        transactionsSet.add(createTransaction(booking.getCustomer(), admin, payment, "Transaction 2: Customer To Admin", amount));

        double newBalance = admin.getBalance() + amount;
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
        booking.setBookingId(generateBookingId());
        booking.setCustomer(customer);
        booking.setTour(tour);
        booking.setPayment(null);
        booking.setDescription(bookingRequest.getDescription());
        if (bookingRequest.getNumberOfAttendees() < 1) {
            throw new InvalidRequestException("Number of attendees must be greater than 0!");
        }
        booking.setNumberOfAttendances(bookingRequest.getNumberOfAttendees());
        booking.setHasVisa(bookingRequest.isHasVisa());
        booking.setBookingStatus(BookingStatus.UNCHECKED);
        booking.setCreatedDate(LocalDateTime.now());

        return booking;

    }

    private void updateCustomerDetails(Account customer, BookingRequest bookingRequest) {
        if ((customer.getPhone() == null || customer.getPhone().isEmpty())) {
            if (bookingRequest.getPhone() != null && !bookingRequest.getPhone().isEmpty()) {
                customer.setPhone(bookingRequest.getPhone());
            } else {
                throw new ActionException("Please enter phone number if you want to register for a tour!");
            }
        } else {
            customer.setPhone(bookingRequest.getPhone());
        }
        if (customer.getFullName() == null || customer.getFullName().isEmpty()) {
            if (bookingRequest.getFullName() != null && !bookingRequest.getFullName().isEmpty()) {
                customer.setFullName(bookingRequest.getFullName());
            } else {
                throw new ActionException("Please enter your full name if you want to register for a tour!");
            }
        } else {
            customer.setFullName(bookingRequest.getFullName());
        }
        accountRepository.save(customer);
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

    private void getConsulting(Account consulting, Booking booking) {
        if (consulting == null || !consulting.getRole().equals(Role.CONSULTING)) {
            throw new ActionException("Only consulting staff can take this action!");
        }
        if (!consulting.getUserId().equals(booking.getTour().getConsulting().getUserId())) {
            throw new ActionException("You are not allowed to take action on this Booking!");
        }
    }

    private void sendBookingConfirmation(Account account, Booking booking) {
        EmailDetail emailDetail = createEmailDetail(account, booking, "Xác nhận đơn đặt Tour", "https://google.com/");
        emailService.sendBookingCompleteEmail(emailDetail);
    }

    private void sendBookingPayment(Account account, Booking booking) {
        EmailDetail emailDetail = createEmailDetail(account, booking, "Thanh toán đơn đặt Tour", "https://google.com/");
        emailService.sendBookingPaymentEmail(emailDetail);
    }

    private EmailDetail createEmailDetail(Account account, Booking booking, String subject, String link) {
        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setAccount(account);
        emailDetail.setBooking(booking);
        emailDetail.setSubject(subject);
        emailDetail.setLink(link);

        return emailDetail;
    }

    public String generateBookingId() {
        return "B" + UUID.randomUUID();
    }

}

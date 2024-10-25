package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.*;
import com.project.KoiBookingSystem.enums.*;
import com.project.KoiBookingSystem.exception.ActionException;
import com.project.KoiBookingSystem.exception.EmptyListException;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.exception.PaymentException;
import com.project.KoiBookingSystem.model.request.BookingAvailableRequest;
import com.project.KoiBookingSystem.model.request.BookingRequest;
import com.project.KoiBookingSystem.model.response.BookingAvailableResponse;
import com.project.KoiBookingSystem.model.response.BookingResponse;
import com.project.KoiBookingSystem.model.response.CustomerOfBookingResponse;
import com.project.KoiBookingSystem.model.response.EmailDetail;
import com.project.KoiBookingSystem.repository.AccountRepository;
import com.project.KoiBookingSystem.repository.BookingRepository;
import com.project.KoiBookingSystem.repository.PaymentRepository;
import com.project.KoiBookingSystem.repository.TourRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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

    @Autowired
    ModelMapper modelMapper;

//    @Transactional
//    public BookingResponse createNewAvailableBooking(BookingRequest bookingRequest, String tourId) {
//        Account customer = getAccount(bookingRequest);
//        Tour tour = getAvailableTour(tourId, bookingRequest.getNumberOfAttendees());
//
//        Booking booking = createBookingDefault(customer, tour, bookingRequest);
//        booking.setSales(null);
//        booking.setTotalPrice(tour.getPrice() * bookingRequest.getNumberOfAttendees());
//        booking.setRequestStatus(RequestStatus.NULL);
//
//        Booking newBooking = bookingRepository.save(booking);
//        tour.setRemainSeat(tour.getRemainSeat() - newBooking.getNumberOfAttendances());
//        tourRepository.save(tour);
//        return convertToBookingResponse(newBooking);
//    }

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
        accountRepository.save(customer);
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
        bookingResponse.setBookingStatus(booking.getBookingStatus());

        return bookingResponse;
    }

    @Scheduled(fixedRate = 60000) // 1 HOUR = 3600000
    public void expireBooking() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findBookingsToExpire(now.minusMinutes(20));

        List<Booking> bookingsToUpdate = new ArrayList<>();
        List<Tour> toursToUpdate = new ArrayList<>();

        for (Booking booking : bookings) {
            booking.setExpired(true);
            booking.setBookingStatus(BookingStatus.CANCELLED);
            bookingsToUpdate.add(booking);

            if (booking.getTour() != null) {
                if (booking.getTour().getType().equals(TourType.REQUESTED_TOUR) && booking.getTour().getStatus() != TourStatus.CANCELLED) {
                    booking.getTour().setStatus(TourStatus.CANCELLED);
                    toursToUpdate.add(booking.getTour());
                }
            }
        }
        if (!bookingsToUpdate.isEmpty()) {
            bookingRepository.saveAll(bookingsToUpdate);
        }
        if (!toursToUpdate.isEmpty()) {
            tourRepository.saveAll(toursToUpdate);
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
        booking.setBookingId(generateBookingId());
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
    //.
    private void updateCustomerDetails(Account customer, BookingRequest bookingRequest) {
        if (customer.getPhone() == null || customer.getPhone().isEmpty()) {
            if (bookingRequest.getPhone() != null && !bookingRequest.getPhone().isEmpty()) {
                customer.setPhone(bookingRequest.getPhone());
            } else {
                throw new ActionException("Please enter phone number if you want to register for a tour!");
            }
        } else if (!customer.getPhone().equals(bookingRequest.getPhone())) {
            throw new ActionException("Logged-in user's phone number does not match the provided phone number!");
        }

        // Kiểm tra và cập nhật tên đầy đủ của khách hàng nếu cần
        if (customer.getFullName() == null || customer.getFullName().isEmpty() || !customer.getFullName().equals(bookingRequest.getFullName())) {
            customer.setFullName(bookingRequest.getFullName());
        }

        accountRepository.save(customer);
    }


//    private Tour getAvailableTour(String tourId, int numberOfAttendees) {
//        Tour tour = tourRepository.findTourByTourId(tourId);
//        if (tour == null) {
//            throw new NotFoundException("Tour Not Found!");
//        }
//        if (!tour.getType().equals(TourType.AVAILABLE_TOUR)) {
//            throw new ActionException("Tour is not available for booking!");
//        }
//        if (tour.getRemainSeat() < numberOfAttendees) {
//            throw new ActionException("Not enough available seat for this tour!");
//        }
//        return tour;
//    }

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
        if (!consulting.equals(booking.getTour().getConsulting())) {
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
        return UUID.randomUUID().toString();
    }
//// nhận yêu cầu booking của khách hàng
//    // yêu cầu booking ban đầu của khách sẽ không có tour

        // ĐẶT TOUR CÓ SẴN.
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


                // Tạo booking mới
                Booking booking = new Booking();
                booking.setBookingId(generateBookingId());  // Tạo BookingID
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


        //hàm để check lỗi username và phone
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
            if (customerInfo.getPhone() == null || customerInfo.getPhone().isEmpty()) {
                throw new ActionException("Customer phone number is required!");
            } else if (!currentCustomer.getPhone().equals(customerInfo.getPhone())) {
                currentCustomer.setPhone(customerInfo.getPhone());
                accountRepository.save(currentCustomer);
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
                    payment.setPaymentId(paymentService.generatePaymentId()); // Gán paymentId mới
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
            payment.setPaymentId(paymentService.generatePaymentId()); // Gán paymentId mới
            payment.setBooking(booking);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setMethod(PaymentMethod.BANKING);
            payment.setStatus(PaymentStatus.PENDING); // Trạng thái ban đầu là PENDING
            payment.setPaymentType(PaymentType.TOUR);
            payment.setDescription("Thanh toán Tour có sẵn!!");
            payment.setCurrency(PaymentCurrency.VND);
            payment.setPrice(booking.getTotalPrice());

            Set<Transactions> setTransactions = new HashSet<>();

            // Tạo các Transactions cho giao dịch
            // 1. Từ VNPAY -> CUSTOMER
            Account customer = authenticationService.getCurrentAccount();
            Transactions transaction1 = new Transactions();
            transaction1.setFromAccount(null);
            transaction1.setToAccount(customer);
            transaction1.setPayment(payment);
            transaction1.setStatus(TransactionsEnum.SUCCESS);
            transaction1.setAmount(0);
            transaction1.setDescription("VNPay TO CUSTOMER");
            setTransactions.add(transaction1);

            // 2. Từ CUSTOMER -> ADMIN
            Account admin = accountRepository.findAccountByRole(Role.ADMIN);
            Transactions transaction2 = new Transactions();
            transaction2.setFromAccount(customer);
            transaction2.setToAccount(admin);
            transaction2.setPayment(payment);
            transaction2.setStatus(TransactionsEnum.SUCCESS);
            transaction2.setDescription("CUSTOMER TO ADMIN");
            transaction2.setAmount(booking.getTotalPrice());

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

        // update thông tin ticket khi khách hàng hướng đến sân bay.
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
                booking.setBookingId(generateBookingId());
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
                tour.setType(TourType.AVAILABLE_TOUR);
                tourRepository.save(tour);

                validateCustomerInfo(bookingAvailableRequest, customer);

                // Tạo payment
                Payment payment = new Payment();
                payment.setBooking(booking);
                payment.setPaymentDate(LocalDateTime.now());
                payment.setMethod(PaymentMethod.CASH);
                payment.setPaymentId(paymentService.generatePaymentId()); // Gán paymentId mới
                payment.setStatus(PaymentStatus.COMPLETED); // Trạng thái ban đầu là PENDING
                payment.setPaymentType(PaymentType.TOUR);
                payment.setCurrency(PaymentCurrency.VND);
                payment.setDescription("Pay by Cash");
                payment.setCurrency(PaymentCurrency.VND);
                payment.setPrice(booking.getTotalPrice());
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

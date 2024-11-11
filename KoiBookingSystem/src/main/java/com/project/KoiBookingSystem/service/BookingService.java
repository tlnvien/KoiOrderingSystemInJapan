package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.*;
import com.project.KoiBookingSystem.enums.*;
import com.project.KoiBookingSystem.exception.*;
import com.project.KoiBookingSystem.model.request.BookingAvailableRequest;
import com.project.KoiBookingSystem.model.request.BookingRequest;
import com.project.KoiBookingSystem.model.response.BookingAvailableResponse;
import com.project.KoiBookingSystem.model.response.BookingResponse;
import com.project.KoiBookingSystem.model.response.CustomerOfBookingResponse;
import com.project.KoiBookingSystem.model.response.EmailDetail;
import com.project.KoiBookingSystem.repository.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
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


    @Autowired
    BookingDetailRepository bookingDetailRepository;

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


    private boolean isMoreThanOrEquals18YearsOld(LocalDate dob) {
        if (dob == null) {
            throw new ActionException("Ngày sinh của khách hàng không được để trống!");
        }
        Period age = Period.between(dob, LocalDate.now());
        return age.getYears() >= 18;
    }

//// nhận yêu cầu booking của khách hàng
//    // yêu cầu booking ban đầu của khách sẽ không có tour

    //=======================TOUR CÓ SẴN
    @Transactional
    public BookingAvailableResponse createTicket(BookingAvailableRequest bookingRequest) {
        try {
            // Tìm tour theo ID
            Tour tour = tourRepository.findTourByTourId(bookingRequest.getTourID());
            if (tour == null) {
                throw new NotFoundException("Tour không tìm thấy!!!");
            }

            // Kiểm tra trạng thái của tour, nếu là IN_PROGRESS thì không cho phép đặt vé
            if (tour.getStatus() == TourStatus.IN_PROGRESS || tour.getStatus() == TourStatus.COMPLETED || tour.getStatus() == TourStatus.CANCELLED) {
                throw new NotFoundException("Tour hiện tại đang trong quá trình xử lý, không thể đặt vé.");
            }

            // Kiểm tra xem khách hàng có tồn tại và có vai trò "CUSTOMER"
            Account currentCustomer = authenticationService.getCurrentAccount();
            if (currentCustomer == null || !currentCustomer.getRole().equals(Role.CUSTOMER)) {
                throw new NotFoundException("Không tìm thấy thông tin khách hàng!!!");
            }

            // Kiểm tra số ghế còn lại của tour
            int requestedSeats = bookingRequest.getNumberOfAttendances();
            if (tour.getRemainSeat() < requestedSeats) {
                throw new NotFoundException("Tour không đủ chỗ để đặt!");
            }

//            // check tuổi từng khách hàng
//            boolean isAdultPresent = false;
//            for (CustomerOfBookingResponse customer : bookingRequest.getCustomers()) {
//                if (customer.getDob() != null && isMoreThanOrEquals18YearsOld(customer.getDob())) {
//                    isAdultPresent = true; // Có ít nhất một người đủ 18 tuổi
//                    break; // Dừng vòng lặp khi tìm thấy người lớn
//                }
//            }
//
//            // Nếu không có ai đủ 18 tuổi, ném ngoại lệ
//            if (!isAdultPresent) {
//                throw new InvalidRequestException("Ít nhất một khách hàng phải đủ 18 tuổi để đặt vé !!!");
//            }
            // Kiểm tra tuổi từng khách hàng, tất cả phải trên 18 tuổi
            for (CustomerOfBookingResponse customer : bookingRequest.getCustomers()) {
                if (customer.getDob() != null && !isMoreThanOrEquals18YearsOld(customer.getDob())) {
                    throw new NotFoundException("Tất cả khách hàng phải đủ 18 tuổi để đặt vé!!!");
                }
            }


//            // kiểm tra tuổi
//            validateAgeRequirement(bookingRequest.getCustomers());


            // Kiểm tra thông tin từng khách hàng trong danh sách
            validateCustomerInfo(bookingRequest.getCustomers(), currentCustomer, requestedSeats);

            // Tạo booking mới
            Booking booking = new Booking();
            booking.setBookingId(generateBookingId());  // Tạo BookingID
            booking.setBookingStatus(BookingStatus.UNCHECKED);  // Trạng thái booking chưa check-in
            booking.setNumberOfAttendances(requestedSeats);
            booking.setCreatedDate(LocalDateTime.now());
            booking.setHasVisa(bookingRequest.isHasVisa());
            booking.setRequestStatus(RequestStatus.NULL);
            booking.setExpired(false);
            booking.setCustomer(currentCustomer);
            booking.setTour(tour);

            // Tính tổng giá tour
            float totalPrice = calculateTotalPrice(tour, bookingRequest.getNumberOfAttendances());
            booking.setTotalPrice(totalPrice);

            // Tạo danh sách BookingDetail
            List<BookingDetail> bookingDetails = new ArrayList<>();
            for (CustomerOfBookingResponse customer : bookingRequest.getCustomers()) {
                BookingDetail bookingDetail = new BookingDetail();
                bookingDetail.setBooking(booking);
                bookingDetail.setCustomerName(customer.getFullName());
                bookingDetail.setPhone(customer.getPhone());
                bookingDetail.setDob(customer.getDob());
                bookingDetail.setGender(customer.getGender());

                bookingDetails.add(bookingDetail);
            }
            booking.setBookingDetails(bookingDetails); // Gán danh sách BookingDetail cho booking

            // Lưu booking
            bookingRepository.save(booking);
            bookingDetailRepository.saveAll(bookingDetails);

            // Lập lịch kiểm tra trạng thái thanh toán
            scheduleBookingCancellation(booking.getBookingId());

            BookingAvailableResponse response = modelMapper.map(booking, BookingAvailableResponse.class);
            response.setPaymentStatus(PaymentStatus.PENDING);
            response.setCustomers(bookingRequest.getCustomers()); // Thêm danh sách customers vào response
            // Trả về response của booking
            return response;
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException(e.getMessage());
        }
    }


//    private void validateAgeRequirement(List<CustomerOfBookingResponse> customers) {
//        boolean isAdultPresent = customers.stream()
//                .filter(customer -> customer.getDob() != null) // Kiểm tra rằng dob không phải là null
//                .anyMatch(customer -> CheckAgeForBooking(customer.getDob()));
//
//        if (!isAdultPresent) {
//            throw new NotFoundException("Ít nhất một khách hàng phải đủ 18 tuổi để đặt vé cho gia đình!");
//        }
//    }
//
//    private boolean CheckAgeForBooking(LocalDate dob) {
//        if (dob == null) {
//            throw new NotFoundException("Ngày sinh của khách hàng không được để trống!");
//        }
//        Period age = Period.between(dob, LocalDate.now());
//        return age.getYears() >= 18;
//    }

    private void validateCustomerInfo(List<CustomerOfBookingResponse> customers, Account currentCustomer, int numberOfAttendances) {
        try {

            // Kiểm tra nếu số khách tham gia khác với số lượng khách hàng được cung cấp
            if (numberOfAttendances != customers.size()) {
                throw new NotFoundException("Số lượng khách hàng không khớp với số người tham gia đã nhập.");
            }

            boolean updated = false;
            Set<String> phoneNumbers = new HashSet<>();

            for (CustomerOfBookingResponse customerInfo : customers) {
                if (customerInfo == null || customerInfo.getFullName() == null || customerInfo.getFullName().isEmpty()) {
                    throw new IllegalArgumentException("Yêu cầu nhập tên đầy đủ của từng khách hàng!");
                }

                // Kiểm tra ngày sinh
                if (customerInfo.getDob() == null) {
                    throw new NotFoundException("Ngày sinh của khách hàng không được để trống!");
                }


                // Kiểm tra và cập nhật nếu thông tin thiếu
                if (currentCustomer.getFullName() == null || currentCustomer.getFullName().isEmpty()) {
                    currentCustomer.setFullName(customerInfo.getFullName());
                    updated = true;
                }
                // Kiểm tra số điện thoại trống hoặc trùng
                if (customerInfo.getPhone() == null || customerInfo.getPhone().isEmpty()) {
                    throw new NotFoundException("Số điện thoại không được để trống hoặc sai thông tin!");
                } else if (!phoneNumbers.add(customerInfo.getPhone())) {
                    throw new NotFoundException("Số điện thoại '" + customerInfo.getPhone() + "' bị trùng lặp giữa các khách hàng!");
                } else if (currentCustomer.getPhone() == null || currentCustomer.getPhone().isEmpty()) {
                    currentCustomer.setPhone(customerInfo.getPhone());
                    updated = true;
                }

                if (currentCustomer.getGender() == null && customerInfo.getGender() != null) {
                    currentCustomer.setGender(customerInfo.getGender());
                    updated = true;
                }
                // Kiểm tra ngày sinh
                if (currentCustomer.getDob() == null && customerInfo.getDob() != null) {
                    currentCustomer.setDob(customerInfo.getDob());
                    updated = true;
                }
            }
            // Lưu vào cơ sở dữ liệu chỉ khi có thay đổi
            if (updated) {
                accountRepository.save(currentCustomer);
            }

        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("phone")) {
                throw new DuplicatedException("Số điện thoại này đã tồn tại trong hệ thống!");
            } else {
                throw new ActionException(e.getMessage());
            }
        }
    }

    //kiểm tra thanh toán
    public boolean isPaymentCompleted(String bookingID) {
        Booking booking = (Booking) bookingRepository.findByBookingId(bookingID)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thông tin vé"));

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
                            .orElseThrow(() -> new NotFoundException("Không tìm thấy thông tin vé"));

                    // Kiểm tra nếu trạng thái thanh toán vẫn là UNPAID
                    if (booking.getBookingStatus() == BookingStatus.UNCHECKED) {
                        // Hủy booking
                        booking.setBookingStatus(BookingStatus.CANCELLED);
                        booking.setExpired(true);
                        bookingRepository.save(booking);  // Lưu lại trạng thái hủy của booking

                    }
                } else {
                    System.out.println("Việc thanh toán đã hoàn tất, không được hủy!!!");
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

    @Transactional
    public void createTransaction(String bookingID) {
        // Tìm Booking
        Booking newBooking = (Booking) bookingRepository.findByBookingId(bookingID)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đặt phòng"));

        //tìm tour của cái booking đó
        Tour tour = tourRepository.findTourByTourId(newBooking.getTour().getTourId());

        Account oldCustomer = authenticationService.getCurrentAccount();
        if (oldCustomer == null) {
            throw new IllegalArgumentException("Khách hàng không hợp lệ.");
        }

        // Kiểm tra nếu BookingStatus là CANCELLED
        if (newBooking.getBookingStatus() == BookingStatus.CANCELLED) {
            // Nếu trạng thái đặt phòng là CANCELLED, đặt PaymentStatus là CANCELLED
            Payment payment = newBooking.getPayment();
            if (payment == null) {
                payment = new Payment(); // Tạo payment mới nếu chưa có
                payment.setPaymentId(paymentService.generatePaymentId()); // Gán paymentId mới
                payment.setBooking(newBooking);
            }

            // Đặt trạng thái Payment là CANCELLED
            payment.setStatus(PaymentStatus.CANCELLED);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setMethod(PaymentMethod.BANKING);
            payment.setPaymentType(PaymentType.TOUR);
            payment.setDescription("Thanh toán cho việc hủy đặt phòng");
            payment.setCurrency(PaymentCurrency.VND);

            // Lưu Payment
            paymentRepository.save(payment);
            throw new ActionException("Đặt phòng đã bị hủy, thanh toán cũng bị hủy.");
        }

        // Kiểm tra nếu đã thanh toán
        Payment existingPayment = newBooking.getPayment();
        if (existingPayment != null && existingPayment.getStatus() == PaymentStatus.COMPLETED) {
            throw new ActionException("Đặt phòng đã được thanh toán trước đó, không thể thanh toán lại.");
        }

        // Tạo Payment cho các booking không bị hủy, trạng thái ban đầu là PENDING
        Payment payment = new Payment();
        payment.setPaymentId(paymentService.generatePaymentId()); // Gán paymentId mới
        payment.setBooking(newBooking);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setMethod(PaymentMethod.BANKING);
        payment.setStatus(PaymentStatus.PENDING); // Trạng thái ban đầu là PENDING
        payment.setPaymentType(PaymentType.TOUR);
        payment.setDescription("Thanh toán Tour có sẵn!!");
        payment.setCurrency(PaymentCurrency.VND);
        payment.setPrice(newBooking.getTotalPrice());

        Set<Transactions> setTransactions = new HashSet<>();

        Account customer = authenticationService.getCurrentAccount();
        if (customer == null) {
            throw new IllegalArgumentException("Khách hàng không hợp lệ.");
        }

        // Tạo các Transactions cho giao dịch
        // 1. Từ VNPAY -> CUSTOMER
        Transactions transaction1 = new Transactions();
        transaction1.setFromAccount(null);
        transaction1.setToAccount(customer);
        transaction1.setPayment(payment);
        transaction1.setStatus(TransactionsEnum.SUCCESS);
        transaction1.setAmount(0); // Hoặc gán giá trị phù hợp nếu cần
        transaction1.setDescription("Giao dịch từ VNPay tới Customer");
        setTransactions.add(transaction1);

        // 2. Từ CUSTOMER -> ADMIN
        Account admin = accountRepository.findAccountByRole(Role.ADMIN);
        if (admin == null) {
            throw new NotFoundException("Admin không tồn tại.");
        }

        Transactions transaction2 = new Transactions();
        transaction2.setFromAccount(customer);
        transaction2.setToAccount(admin);
        transaction2.setPayment(payment);
        transaction2.setStatus(TransactionsEnum.SUCCESS);
        transaction2.setDescription("Giao dịch từ Customer tới Admin");
        transaction2.setAmount(newBooking.getTotalPrice());

        // Cập nhật số dư cho ADMIN
        float newBalance = (float) (admin.getBalance() + newBooking.getTotalPrice());
        admin.setBalance(newBalance);
        setTransactions.add(transaction2);

        // Lưu thông tin vào Payment và Booking
        payment.setTransactions(setTransactions);
        payment.setStatus(PaymentStatus.COMPLETED); // Sau khi các giao dịch thành công
        newBooking.setBookingStatus(BookingStatus.NOT_CONFIRMED);
        newBooking.setPayment(payment);
        newBooking.setExpired(false);

        // Cập nhật số ghế còn lại của tour sau khi thanh toán
        int updatedRemainSeats = tour.getRemainSeat() - newBooking.getNumberOfAttendances();
        tour.setRemainSeat(updatedRemainSeats);


        // Lưu các đối tượng
        paymentRepository.save(payment);
        bookingRepository.save(newBooking);
        accountRepository.save(admin); // Cần phải lưu ADMIN sau khi cập nhật số dư
        tourRepository.save(tour); // Cập nhật số ghế sau khi thanh toán


        // Gửi email xác nhận
        sendBookingConfirmation(customer, newBooking);
    }


    // lấy danh sách booking dựa vào mã userID
    @Transactional
    public List<BookingAvailableResponse> getAllBookings(String userID) {
        // Kiểm tra xem tài khoản có tồn tại hay không
        Account account = accountRepository.findAccountByUserId(userID);
        if (account == null) {
            throw new NotFoundException("Không tìm thấy tài khoản");
        }

        // Tìm tất cả các booking của customer bằng tài khoản
        List<Booking> bookings = bookingRepository.findBookingsByCustomer(account);


        // Kiểm tra nếu không có booking nào
        if (bookings.isEmpty()) {
            throw new NotFoundException("Không có vé nào được đặt bởi tài khoản này");
            //No bookings found for this account
        }

        // Chuyển đổi danh sách booking thành danh sách BookingResponse sử dụng ModelMapper
        return bookings.stream()
                .map(booking -> {
                    BookingAvailableResponse response = modelMapper.map(booking, BookingAvailableResponse.class);
                    // Lấy danh sách khách hàng từ bookingDetails
                    List<CustomerOfBookingResponse> customers = booking.getBookingDetails().stream()
                            .map(bookingDetail -> {
                                CustomerOfBookingResponse customerResponse = new CustomerOfBookingResponse();
                                customerResponse.setFullName(bookingDetail.getCustomerName());
                                customerResponse.setPhone(bookingDetail.getPhone());
                                customerResponse.setGender(bookingDetail.getGender());
                                customerResponse.setDob(bookingDetail.getDob());
                                return customerResponse;
                            })
                            .collect(Collectors.toList());

                    response.setCustomers(customers);
                    // Cập nhật paymentStatus dựa trên trạng thái booking
                    if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
                        response.setPaymentStatus(PaymentStatus.CANCELLED);
                    } else if (booking.getPayment() != null) {
                        response.setPaymentStatus(booking.getPayment().getStatus());
                    } else {
                        response.setPaymentStatus(PaymentStatus.PENDING);
                    }
                    return response;
                })
                .collect(Collectors.toList());
    }


    // lấy danh sách booking IsExpire
    @Transactional
    public List<BookingAvailableResponse> getExpiredBookings(String tourId) {
        // Tìm tất cả các booking có isExpired = fales
        List<Booking> expiredBookings = bookingRepository.findBookingsByIsExpiredAndTour_TourId(false, tourId);

        // Kiểm tra nếu không có booking nào
        if (expiredBookings.isEmpty()) {
            throw new NotFoundException("vé đã hết hạn");
        }

        // Chuyển đổi danh sách booking thành danh sách BookingResponse sử dụng ModelMapper
        return expiredBookings.stream()
                .map(booking -> {
                    BookingAvailableResponse response = modelMapper.map(booking, BookingAvailableResponse.class);


                    // Lấy danh sách khách hàng từ bookingDetails
                    List<CustomerOfBookingResponse> customers = booking.getBookingDetails().stream()
                            .map(bookingDetail -> {
                                CustomerOfBookingResponse customerResponse = new CustomerOfBookingResponse();
                                customerResponse.setFullName(bookingDetail.getCustomerName());
                                customerResponse.setPhone(bookingDetail.getPhone());
                                customerResponse.setGender(bookingDetail.getGender());
                                customerResponse.setDob(bookingDetail.getDob());
                                return customerResponse;
                            })
                            .collect(Collectors.toList());

                    response.setCustomers(customers);
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
}
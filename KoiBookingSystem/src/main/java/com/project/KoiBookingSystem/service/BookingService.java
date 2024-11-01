package com.project.KoiBookingSystem.service;


import com.project.KoiBookingSystem.entity.*;
import com.project.KoiBookingSystem.enums.*;
import com.project.KoiBookingSystem.exception.*;
import com.project.KoiBookingSystem.model.request.BookingAvailableRequest;
import com.project.KoiBookingSystem.model.request.BookingRequest;
import com.project.KoiBookingSystem.model.response.*;
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
import java.text.NumberFormat;
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
    BookingDetailRepository bookingDetailRepository;

    @Autowired
    ModelMapper modelMapper;


    @Transactional
    public BookingResponse createNewRequestedBooking(BookingRequest bookingRequest) {
        Account customer = getAccount(bookingRequest);

        Booking booking = createBookingDefault(customer, null, bookingRequest);
        try {
            booking.setSales(null);
            booking.setRequestStatus(RequestStatus.NOT_TAKEN);
            booking.setTotalPrice(0);

            Booking newBooking = bookingRepository.save(booking);
            List<BookingDetail> bookingDetails = createBookingDetails(bookingRequest, newBooking);

            if (bookingDetails.size() != newBooking.getNumberOfAttendances() - 1) {
                throw new InvalidRequestException("Thông tin chi tiết của booking phải được nhập tương đương với số người mà bạn đăng ký!");
            }
            bookingDetailRepository.saveAll(bookingDetails);
            newBooking.setBookingDetails(bookingDetails);
            Booking savedBooking = bookingRepository.save(newBooking);
            return convertToBookingResponse(savedBooking);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidRequestException(e.getMessage());
        }
    }


    public List<BookingResponse> getAllRequests() {
        List<Booking> requests = bookingRepository.findByRequestStatusAndIsExpiredFalse(RequestStatus.NOT_TAKEN);
        if (requests.isEmpty()) {
            throw new EmptyListException("Không có yêu cầu đặt tour cần được giải quyết trong hệ thống!");
        }
        return requests.stream().map(this::convertToBookingResponse).collect(Collectors.toList());
    }


    public List<BookingResponse> getBookingsBySales(String userId) {
        List<Booking> bookings = bookingRepository.findBySales_UserId(userId);
        if (bookings.isEmpty()) {
            throw new EmptyListException("Nhân viên kinh doanh này chưa nhận bất kỳ yêu cầu nào trong hệ thống!");
        }
        return bookings.stream().map(this::convertToBookingResponse).collect(Collectors.toList());
    }

    public List<BookingResponse> getAllBookingInfoByConsulting() {
        Account consulting = authenticationService.getCurrentAccount();
        if (consulting == null || consulting.getRole() != Role.CONSULTING) {
            throw new AuthorizationException("Hành động này chỉ có nhân viên tư vấn mới có thể thực hiện!");
        }
        List<Booking> bookings = bookingRepository.findByTour_Consulting_UserId(consulting.getUserId());
        if (bookings.isEmpty()) {
            throw new EmptyListException("Bạn hiện chưa đang ở trong bất kỳ tour nào!");
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
        if (booking.getBookingStatus() != BookingStatus.UNCHECKED && booking.getBookingStatus() != BookingStatus.NOT_CONFIRMED) {
            throw new InvalidRequestException("Booking này không thể được check in!");
        }
        booking.setBookingStatus(status);
        booking.setCheckingDate(LocalDateTime.now());
        Booking checkedBooking = bookingRepository.save(booking);
        return convertToBookingResponse(checkedBooking);
    }

    // NHẬN REQUEST TỪ THẰNG SALES
    @Transactional
    public BookingResponse takeRequestBooking(String bookingId) {
        Account sales = getSales();

        Booking booking = bookingRepository.findBookingByBookingId(bookingId);
        validateBooking(booking);

        try {
            if (booking.getSales() != null) {
                throw new InvalidRequestException("Yêu cầu đặt tour này đã hoặc đang được thực hiện bởi một nhân viên kinh doanh khác!");
            }
            if (booking.getRequestStatus() != (RequestStatus.NOT_TAKEN)) {
                throw new InvalidRequestException("Yêu cầu đặt tour này không thể được nhận để giải quyết!");
            }
            booking.setRequestStatus(RequestStatus.IN_PROGRESS);
            booking.setSales(sales);
            Booking takenBooking = bookingRepository.save(booking);
            return convertToBookingResponse(takenBooking);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidRequestException(e.getMessage());
        }
    }

    // LIÊN KẾT BOOKING ĐÃ YÊU CẦU VỚI TOUR ĐƯỢC TẠO BỞI THẰNG NHÂN VIÊN KINH DOANH
    @Transactional
    public BookingResponse associateBookingToRequestedTour(String bookingId, String tourId) {

        Booking booking = bookingRepository.findBookingByBookingId(bookingId);

        validateBooking(booking);
        if (booking.getSales() == null) {
            throw new InvalidRequestException("Booking này chưa thể liên kết với bất kỳ tour theo yêu cầu nào vì không tìm thấy nhân viên kinh doanh xử lý!");
        }
        if (booking.getTour() != null) {
            throw new InvalidRequestException("Booking này đã được liên kết với một tour khác!");
        }
        Tour tour = getRequestTour(tourId);
        if (tour.getStatus() != TourStatus.NOT_YET) {
            throw new InvalidRequestException("Tour này đã được bắt đầu hoặc kết thúc, không thể được liên kết!");
        }
        Account sales = getSales();
        if (!sales.getUserId().equals(booking.getSales().getUserId()) && !sales.getUserId().equals(tour.getSales().getUserId())) {
            throw new InvalidRequestException("Bạn không thể liên kết booking này vì tour bạn đang thực hiện không do chính bạn tạo ra!");
        }
        try {
            booking.setTour(tour);
            tour.setMaxParticipants(booking.getNumberOfAttendances());
            booking.setTotalPrice(tour.getPrice());
            booking.setRequestStatus(RequestStatus.COMPLETED);
            tourRepository.save(tour);
            Booking associatedBooking = bookingRepository.save(booking);
            sendBookingPayment(associatedBooking.getCustomer(), associatedBooking);
            return convertToBookingResponse(associatedBooking);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidRequestException(e.getMessage());
        }
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
        if (booking.getTour() == null) {
            throw new InvalidRequestException("Booking này chưa thể thực hiện thanh toán vì chưa được liên kết với bất kỳ tour nào!");
        }
        if (booking.getPayment() != null) {
            throw new InvalidRequestException("Booking này đã được thanh toán, không thể thanh toán lại!");
        }
        Payment payment = createBookingPayment(booking);
        try {
            Set<Transactions> transactionsSet = createBookingTransactions(payment, booking);

            payment.setTransactions(transactionsSet);
            payment.setStatus(PaymentStatus.COMPLETED);

            paymentRepository.save(payment);
            booking.setPayment(payment);

            bookingRepository.save(booking);

            sendBookingConfirmation(booking.getCustomer(), booking);
        } catch (Exception e) {
            Tour tour = booking.getTour();
            if (tour.getType() == TourType.AVAILABLE_TOUR) {
                tour.setRemainSeat(tour.getRemainSeat() + booking.getNumberOfAttendances());
                tourRepository.save(tour);
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
            }
            throw new PaymentException("Giao dịch thất bại: " + e.getMessage());
        }
    }

    public String createBookingPaymentUrl(String bookingId) {
        Booking booking = bookingRepository.findBookingByBookingId(bookingId);
        validateBooking(booking);
        if (booking.getTour() == null) {
            throw new InvalidRequestException("Booking này chưa thể thực hiện thanh toán vì chưa được liên kết với bất kỳ tour nào!");
        }
        if (booking.getPayment() != null) {
            throw new InvalidRequestException("Booking này đã được thanh toán, không thể thanh toán lại!");
        }
        double amount = booking.getTotalPrice();
        try {
            return vnPayService.createPaymentUrl(bookingId, amount, "Booking", 0);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new PaymentException("Tạo đường dẫn thanh toán thất bại: " + e.getMessage());
        }
    }


    public BookingResponse getBookingById(String bookingId) {
        Booking booking = bookingRepository.findBookingByBookingId(bookingId);
        validateBooking(booking);
        return convertToBookingResponse(booking);
    }

    public BookingResponse convertToBookingResponse(Booking booking) {
        BookingResponse bookingResponse = new BookingResponse();

        bookingResponse.setBookingId(booking.getBookingId());

        if (booking.getPayment() == null) {
            bookingResponse.setPaymentId("Booking chưa được thanh toán!");
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

        String formattedPrice = NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(booking.getTotalPrice());
        bookingResponse.setTotalPrice(formattedPrice);
        bookingResponse.setStatus(booking.getBookingStatus());
        bookingResponse.setCheckingDate(booking.getCheckingDate());

        List<BookingDetailResponse> bookingDetailResponses = booking.getBookingDetails().stream().map(bookingDetail -> {
            BookingDetailResponse bookingDetailResponse = new BookingDetailResponse();
            bookingDetailResponse.setCustomerName(bookingDetail.getCustomerName());
            bookingDetailResponse.setDob(bookingDetail.getDob());
            bookingDetailResponse.setGender(bookingDetail.getGender());
            bookingDetailResponse.setPhone(bookingDetail.getPhone());

            return bookingDetailResponse;
        }).collect(Collectors.toList());

        bookingResponse.setBookingDetailResponses(bookingDetailResponses);

        return bookingResponse;
    }


    // HÀM NÀY DÙNG ĐỂ TÍNH THỜI GIAN HỦY BOOKING NẾU KHÁCH HÀNG KHÔNG THANH TOÁN, NẾU BOOKING THUỘC VỀ MỘT TOUR THEO YÊU CẦU, TOUR ĐÓ SẼ BỊ HỦY THEO
    @Scheduled(fixedRate = 3600000) // 1 HOUR = 3600000
    public void expireBooking() {
        LocalDateTime now = LocalDateTime.now();

        List<Booking> requestedTourBookings = bookingRepository.findBookingsToExpire(now.minusHours(24), TourType.REQUESTED_TOUR);

        List<Booking> bookingsToUpdate = new ArrayList<>();
        List<Tour> toursToUpdate = new ArrayList<>();


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
            throw new PaymentException("Quá trình giao dịch thất bại!");
        }
    }

    private Payment createBookingPayment(Booking booking) {
        Payment payment = new Payment();
        payment.setPaymentId(paymentService.generatePaymentId());
        payment.setBooking(booking);
        payment.setOrders(null);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setDescription("Thanh toán bằng VNPAY: " + booking.getBookingId());
        payment.setPrice(booking.getTotalPrice());
        payment.setPaymentType(PaymentType.TOUR);
        payment.setMethod(PaymentMethod.BANKING);
        payment.setCurrency(PaymentCurrency.VND);

        return payment;
    }

    private List<BookingDetail> createBookingDetails(BookingRequest bookingRequest, Booking booking) {
        return bookingRequest.getBookingDetailRequests().stream().map(bookingDetailRequest -> {
            BookingDetail bookingDetail = new BookingDetail();
            bookingDetail.setBooking(booking);
            bookingDetail.setCustomerName(bookingDetailRequest.getCustomerName());
            bookingDetail.setDob(bookingDetailRequest.getDob());
            bookingDetail.setGender(bookingDetailRequest.getGender());
            bookingDetail.setPhone(bookingDetailRequest.getPhone());

            return bookingDetail;
        }).collect(Collectors.toList());
    }

    private void validateBooking(Booking booking) {
        if (booking == null) {
            throw new NotFoundException("Không tìm thấy booking với Id yêu cầu!");
        }
        if (booking.isExpired()) {
            throw new InvalidRequestException("Booking đã bị hủy và không thể được thực hiện bất cứ hành động nào!");
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
        if (customer == null || customer.getRole() != Role.CUSTOMER) {
            throw new AuthorizationException("Chỉ có khách hàng mới có thể thực hiện hành động này!");
        }
        checkUserIsPendingDeletion(customer);
        return customer;
    }

    private Account getAdminAccount() {
        Account admin = accountRepository.findAccountByRole(Role.ADMIN);
        if (admin == null) {
            throw new NotFoundException("Không tìm thấy tài khoản admin trong hệ thống!");
        }
        return admin;
    }

    private Booking createBookingDefault(Account customer, Tour tour, BookingRequest bookingRequest) {
        Booking booking = new Booking();
        booking.setBookingId(generateBookingId());
        if (!isMoreThanOrEquals18YearsOld(customer.getDob())) {
            throw new InvalidRequestException("Bạn chưa đủ 18 tuổi để thực hiện việc đặt tour! Để có thể đặt tour, bạn cần phải đi kèm với người lớn!");
        }
        booking.setCustomer(customer);
        booking.setTour(tour);
        booking.setPayment(null);
        booking.setDescription(bookingRequest.getDescription());
        if (bookingRequest.getNumberOfAttendees() < 1) {
            throw new InvalidRequestException("Số lượng người đi tour phải ít nhất là 1!");
        }
        booking.setNumberOfAttendances(bookingRequest.getNumberOfAttendees());
        booking.setHasVisa(bookingRequest.isHasVisa());
        booking.setBookingStatus(BookingStatus.UNCHECKED);
        booking.setCreatedDate(LocalDateTime.now());

        return booking;

    }

    private boolean isMoreThanOrEquals18YearsOld(LocalDate dob) {
        Period age = Period.between(dob, LocalDate.now());
        return age.getYears() >= 18;
    }

    private void updateCustomerDetails(Account customer, BookingRequest bookingRequest) {
        try {
            if (customer.getPhone() == null || customer.getPhone().isEmpty()) {
                if (bookingRequest.getPhone() != null && !bookingRequest.getPhone().isEmpty()) {
                    customer.setPhone(bookingRequest.getPhone());
                } else {
                    throw new InvalidRequestException("Hãy nhập số điện thoại nếu bạn muốn đăng ký tour!");
                }
            }
            if (customer.getFullName() == null || customer.getFullName().isEmpty()) {
                if (bookingRequest.getFullName() != null && !bookingRequest.getFullName().isEmpty()) {
                    customer.setFullName(bookingRequest.getFullName());
                } else {
                    throw new InvalidRequestException("Hãy nhập đầy đủ họ tên để có thể đăng ký tour!");
                }
            }
            if (customer.getDob() == null) {
                if (bookingRequest.getDob() != null) {
                    customer.setDob(bookingRequest.getDob());
                } else {
                    throw new InvalidRequestException("Hãy nhập ngày tháng năm sinh để có thể đăng ký tour!");
                }
            }
            if (customer.getGender() == null) {
                if (bookingRequest.getGender() != null) {
                    customer.setGender(bookingRequest.getGender());
                } else {
                    throw new InvalidRequestException("Hãy nhập giới tính của bạn để có thể đăng ký tour!");
                }
            }
            accountRepository.save(customer);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains(customer.getPhone())) {
                throw new DuplicatedException("Số điện thoại này đã tồn tại trong hệ thống!");
            } else {
                throw new InvalidRequestException(e.getMessage());
            }
        }
    }

    private Tour getRequestTour(String tourId) {
        Tour tour = tourRepository.findTourByTourId(tourId);
        if (tour == null || tour.getType() != TourType.REQUESTED_TOUR) {
            throw new NotFoundException("Không tìm thấy tour với Id mà bạn yêu cầu!");
        }
        if (tour.getTourApproval() != TourApproval.CONFIRMED) {
            throw new InvalidRequestException("Tour này chưa được chấp thuận bởi quản lý!");
        }
        return tour;
    }

    private Account getSales() {
        Account sales = authenticationService.getCurrentAccount();
        if (sales == null || sales.getRole() != Role.SALES) {
            throw new AuthorizationException("Chỉ có nhân viên kinh doanh mới thực hiện được hành động này!");
        }
        return sales;
    }

    private void getConsulting(Account consulting, Booking booking) {
        if (consulting == null || consulting.getRole() != Role.CONSULTING) {
            throw new AuthorizationException("Chỉ có nhân viên tư vấn mới có thể thực hiện hành động này!");
        }
        if (!consulting.getUserId().equals(booking.getTour().getConsulting().getUserId())) {
            throw new InvalidRequestException("Bạn không thể thực hiện hành động trên tour này vì bạn không phải là nhân viên tư vấn của tour!");
        }
    }

    private void sendBookingConfirmation(Account account, Booking booking) {
        EmailDetail emailDetail = createEmailDetail(account, booking, "Xác nhận đơn đặt Tour", null);
        emailService.sendBookingCompleteEmail(emailDetail);
    }

    private void sendBookingPayment(Account account, Booking booking) {
        EmailDetail emailDetail = createEmailDetail(account, booking, "Thanh toán đơn đặt Tour", "https://localhost:5173/payment");
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

    private void checkUserIsPendingDeletion(Account account) {
        if (account.isPendingDeletion())
            throw new AuthenticationException("Tài khoản " + account.getUserId() + " đang yêu cầu xóa và không thể thực hiện bất kỳ hành động nào trong hệ thống. Để hủy quá trình này, vui lòng đăng nhập lại!");
        if (!account.isStatus()) throw new AuthenticationException("Tài khoản này đã không còn tồn tại!");
    }


    //=======================TOUR CÓ SẴN
    @Transactional
    public BookingAvailableResponse createTicket(BookingAvailableRequest bookingRequest) {
        try {
            // Tìm tour theo ID
            Tour tour = tourRepository.findTourByTourId(bookingRequest.getTourID());
            if (tour == null) {
                throw new NotFoundException("Tour không tìm thấy!!!");
            }

            // Kiểm tra xem khách hàng có tồn tại và có vai trò "CUSTOMER"
            Account currentCustomer = authenticationService.getCurrentAccount();
            if (currentCustomer == null || !currentCustomer.getRole().equals(Role.CUSTOMER)) {
                throw new NotFoundException("Không tìm thấy thông tin khách hàng!!!");
            }

            // Kiểm tra số ghế còn lại của tour
            int requestedSeats = bookingRequest.getNumberOfAttendances();
            if (tour.getRemainSeat() < requestedSeats) {
                throw new InvalidRequestException("Tour không đủ chỗ để đặt!");
            }

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


            // CẬP NHẬT BẢNG TOUR
            // Cập nhật số ghế còn lại của tour
            int updatedRemainSeats = tour.getRemainSeat() - requestedSeats;
            tour.setRemainSeat(updatedRemainSeats);
            tourRepository.save(tour);

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


    // Hàm để kiểm tra lỗi tên người dùng và số điện thoại
    private void validateCustomerInfo(List<CustomerOfBookingResponse> customers, Account currentCustomer, int numberOfAttendances) {
        try {

            // Kiểm tra nếu số khách tham gia khác với số lượng khách hàng được cung cấp
            if (numberOfAttendances != customers.size()) {
                throw new InvalidRequestException("Số lượng khách hàng không khớp với số người tham gia đã nhập.");
            }

            boolean updated = false;

            for (CustomerOfBookingResponse customerInfo : customers) {
                if (customerInfo == null || customerInfo.getFullName() == null || customerInfo.getFullName().isEmpty()) {
                    throw new IllegalArgumentException("Yêu cầu nhập tên đầy đủ của từng khách hàng!");
                }

                // Kiểm tra và cập nhật nếu thông tin thiếu
                if (currentCustomer.getFullName() == null || currentCustomer.getFullName().isEmpty()) {
                    currentCustomer.setFullName(customerInfo.getFullName());
                    updated = true;
                }

                if (customerInfo.getPhone() == null || customerInfo.getPhone().isEmpty()) {
                    throw new InvalidRequestException("Số điện thoại không được để trống hoặc sai thông tin!");
                } else if (currentCustomer.getPhone() == null || currentCustomer.getPhone().isEmpty()) {
                    currentCustomer.setPhone(customerInfo.getPhone());
                    updated = true;
                }

                if (currentCustomer.getGender() == null && customerInfo.getGender() != null) {
                    currentCustomer.setGender(customerInfo.getGender());
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
                throw new InvalidRequestException(e.getMessage());
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
            throw new InvalidRequestException("Đặt phòng đã bị hủy, thanh toán cũng bị hủy.");
        }

        // Kiểm tra nếu đã thanh toán
        Payment existingPayment = newBooking.getPayment();
        if (existingPayment != null && existingPayment.getStatus() == PaymentStatus.COMPLETED) {
            throw new InvalidRequestException("Đặt phòng đã được thanh toán trước đó, không thể thanh toán lại.");
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


        // Lưu các đối tượng
        paymentRepository.save(payment);
        bookingRepository.save(newBooking);
        accountRepository.save(admin); // Cần phải lưu ADMIN sau khi cập nhật số dư

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
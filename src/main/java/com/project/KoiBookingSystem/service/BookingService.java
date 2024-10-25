package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.*;
import com.project.KoiBookingSystem.enums.*;
import com.project.KoiBookingSystem.exception.ActionException;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.model.request.BookingRequest;
import com.project.KoiBookingSystem.model.response.BookingResponse;
import com.project.KoiBookingSystem.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.*;

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
    ordersRepository ordersRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private AuthenticationService authenticationService;

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

//    public String createUrl(BookingRequest bookingRequest) throws Exception {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
//        LocalDateTime createDate = LocalDateTime.now();
//        String formattedCreateDate = createDate.format(formatter);
//
//
//        BookingResponse booking = createTicket(bookingRequest);
//        float money = booking.getTotalPrice() * 100;  // để mất thập phân kiểu int (sân nhà của ng ta)
//        String amount = String.valueOf((int) money);
//
//
//        String tmnCode = "K8GYRSRJ";
//        String secretKey = "GZRDJNWZ5DZCJF1PF3WV4MP7YX7ZT8H6";
//        String vnpUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
//        String returnUrl = "https://mail.google.com/mail/u/0/?tab=rm&ogbl#inbox?orderID=" + booking.getBookingId();
//        // trang thông báo thành công (font-end) làm
//        // đính kèm Id cho orders (sản phẩm)
//        String currCode = "VND";
//
//        Map<String, String> vnpParams = new TreeMap<>();
//        vnpParams.put("vnp_Version", "2.1.0");
//        vnpParams.put("vnp_Command", "pay");
//        vnpParams.put("vnp_TmnCode", tmnCode);
//        vnpParams.put("vnp_Locale", "vn");
//        vnpParams.put("vnp_CurrCode", currCode);
//        vnpParams.put("vnp_TxnRef", booking.getBookingId()); //!!! Do Orders id đang là kiểu UUID (String)
//        vnpParams.put("vnp_OrderInfo", "Thanh toan cho ma GD: " + booking.getBookingId());
//        vnpParams.put("vnp_OrderType", "other");
//        vnpParams.put("vnp_Amount", amount);
//        //!!! do vnp_Amount là do người ta quy định, dùng ké thì biết điều tý => phải đúng định dạng
//
//        vnpParams.put("vnp_ReturnUrl", returnUrl);
//        vnpParams.put("vnp_CreateDate", formattedCreateDate);
//        vnpParams.put("vnp_IpAddr", "128.199.178.23");
//
//        StringBuilder signDataBuilder = new StringBuilder();
//        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
//            signDataBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
//            signDataBuilder.append("=");
//            signDataBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
//            signDataBuilder.append("&");
//        }
//        signDataBuilder.deleteCharAt(signDataBuilder.length() - 1); // Remove last '&'
//
//        String signData = signDataBuilder.toString();
//        String signed = generateHMAC(secretKey, signData);
//
//        vnpParams.put("vnp_SecureHash", signed);
//
//        StringBuilder urlBuilder = new StringBuilder(vnpUrl);
//        urlBuilder.append("?");
//        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
//            urlBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
//            urlBuilder.append("=");
//            urlBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
//            urlBuilder.append("&");
//        }
//        urlBuilder.deleteCharAt(urlBuilder.length() - 1); // Remove last '&'
//
//        return urlBuilder.toString();
//    }
//
//    private String generateHMAC(String secretKey, String signData) throws NoSuchAlgorithmException, InvalidKeyException {
//        Mac hmacSha512 = Mac.getInstance("HmacSHA512");
//        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
//        hmacSha512.init(keySpec);
//        byte[] hmacBytes = hmacSha512.doFinal(signData.getBytes(StandardCharsets.UTF_8));
//
//        StringBuilder result = new StringBuilder();
//        for (byte b : hmacBytes) {
//            result.append(String.format("%02x", b));
//        }
//        return result.toString();
//    }

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

}



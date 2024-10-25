package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.*;
import com.project.KoiBookingSystem.enums.OrderStatus;
import com.project.KoiBookingSystem.enums.PaymentEnums;
import com.project.KoiBookingSystem.enums.Role;
import com.project.KoiBookingSystem.enums.TransactionEnums;
import com.project.KoiBookingSystem.exception.EmptyListException;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.model.request.OrderDetailRequest;
import com.project.KoiBookingSystem.model.request.OrderRequest;
import com.project.KoiBookingSystem.model.response.OrderDetailResponse;
import com.project.KoiBookingSystem.model.response.OrderResponse;
import com.project.KoiBookingSystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private KoiRepository koiRepository;

    @Autowired
    ordersRepository ordersRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private FarmRepository farmRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public OrderResponse createOrder(OrderRequest orderRequest) {
        //note
            Account customer = accountRepository.findAccountByUserID(orderRequest.getUserId());
            if (customer == null) {
                throw new NotFoundException("CustomerId Not Found!");
            }
            Orders orders = new Orders();
            //customerId
            orders.setCustomer(customer);
            //bookingId
            orders.setBooking(bookingRepository.findBookingByBookingID(orderRequest.getBookingId()));
            //date
            orders.setDate(new Date());
            //totalPrice
            orders.setTotal(totalPrice(orderRequest.getOrderDetailRequests()));
            List<OrderDetail> orderDetails = new ArrayList<>();
            for (OrderDetailRequest list: orderRequest.getOrderDetailRequests()) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setFarmId(farmRepository.findFarmByFarmID(String.valueOf(list.getFarmId())));//note fix
                orderDetail.setKoi(koiRepository.findKoiByKoiID(String.valueOf(list.getKoiId())));//note fix
                orderDetail.setQuantity(list.getQuantity());
                orderDetail.setPrice(list.getPrice());
                orderDetail.setOrder(orders);
                orderDetails.add(orderDetail);
            }
            //list DetailOrder
            orders.setOrderDetails(orderDetails);
            //payment
            Payment payment = new Payment();
            payment.setOrders(orders);
            payment.setCreatedAt(new Date());
            payment.setPayment_method(PaymentEnums.CASH);
            orders.setPayment(payment);
            //delivering
            orders.setDelivering(accountRepository.findRandomUserIdWithPrefix());
            //status
            orders.setStatus(OrderStatus.PROCESSING);
            ordersRepository.save(orders);
            OrderResponse orderResponse = new OrderResponse();
            orderResponse.setOrderId(orders.getOrderId());
            orderResponse.setCustomerFirstName(customer.getFirstName());
            orderResponse.setCustomerLastName(customer.getLastName());
            orderResponse.setBookingId(orderRequest.getBookingId());
            orderResponse.setDate(orders.getDate());
            orderResponse.setTotal(orders.getTotal());
            orderResponse.setOrderDetails(orders.getOrderDetails());
            orderResponse.setDeliveringName(orders.getDelivering().getFirstName() + " " + orders.getDelivering().getLastName());
            return orderResponse;
    }

    protected int totalPrice(List<OrderDetailRequest> orderDetailRequests) {
        int totalPrice = 0;
        for (OrderDetailRequest orderDetailRequest : orderDetailRequests) {
            totalPrice += orderDetailRequest.getPrice() * orderDetailRequest.getQuantity();
        }
        return totalPrice;
    }

    public List<Orders> getAllOrder() {
        List<Orders> ordersList = ordersRepository.findAll();
        return ordersList;
    }

    public List<Orders> getAllOrderOfCustomer() {
        Account customer = authenticationService.getCurrentAccount();
        List<Orders> ordersList = ordersRepository.findOrderssByCustomer(customer);
        return ordersList;
    }

    public List<Orders> getAllIndividualOrder(String customerId) {
        Account customer = authenticationService.accountRepository.findAccountByUserID(customerId);
        List<Orders> ordersList = ordersRepository.findOrderssByCustomer(customer);
        return ordersList;
    }

    public List<OrderResponse> getAllOrdersByTour(String tourId) {
        List<Orders> orders = ordersRepository.findByTour_TourId(tourId);
        if (orders.isEmpty()) {
            throw new EmptyListException("There is no order in the list!");
        }
        return orders.stream().map(this::convertToOrdersResponse).collect(Collectors.toList());
    }

    public String createUrl(OrderRequest ordersRequest) throws  Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime createDate = LocalDateTime.now();
        String formattedCreateDate = createDate.format(formatter);

        OrderResponse orders = createOrder(ordersRequest);  //enum still CASH
        float money = orders.getTotal();
        String amount = String.valueOf((int) money);

        String tmnCode = "K8GYRSRJ";
        String secretKey = "GZRDJNWZ5DZCJF1PF3WV4MP7YX7ZT8H6";
        String vnpUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
        String returnUrl = "https://blearning.vn/guide/swp/docker-local?orderID=" + orders.getOrderId();
        // trang thông báo thành công (font-end) làm
        // đính kèm Id cho orders (sản phẩm)
        String currCode = "VND";

        Map<String, String> vnpParams = new TreeMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", tmnCode);
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_CurrCode", currCode);
        vnpParams.put("vnp_TxnRef", orders.getOrderId().toString()); //!!! Do Orders id đang là kiểu UUID (String)
        vnpParams.put("vnp_OrderInfo", "Thanh toan cho ma GD: " + orders.getOrderId());
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Amount",amount);
        //!!! do vnp_Amount là do người ta quy định, dùng ké thì biết điều tý => phải đúng định dạng

        vnpParams.put("vnp_ReturnUrl", returnUrl);
        vnpParams.put("vnp_CreateDate", formattedCreateDate);
        vnpParams.put("vnp_IpAddr", "128.199.178.23");

        StringBuilder signDataBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            signDataBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            signDataBuilder.append("=");
            signDataBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            signDataBuilder.append("&");
        }
        signDataBuilder.deleteCharAt(signDataBuilder.length() - 1); // Remove last '&'

        String signData = signDataBuilder.toString();
        String signed = generateHMAC(secretKey, signData);

        vnpParams.put("vnp_SecureHash", signed);

        StringBuilder urlBuilder = new StringBuilder(vnpUrl);
        urlBuilder.append("?");
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            urlBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            urlBuilder.append("=");
            urlBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            urlBuilder.append("&");
        }
        urlBuilder.deleteCharAt(urlBuilder.length() - 1); // Remove last '&'

        return urlBuilder.toString();
    }

    private String generateHMAC(String secretKey, String signData) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSha512 = Mac.getInstance("HmacSHA512");
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmacSha512.init(keySpec);
        byte[] hmacBytes = hmacSha512.doFinal(signData.getBytes(StandardCharsets.UTF_8));

        StringBuilder result = new StringBuilder();
        for (byte b : hmacBytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    public void createTransaction (UUID uuid){
        //tìm cái order
        Orders orders = ordersRepository.findById(uuid)
                .orElseThrow(() -> new NotFoundException("Order not found"));


        //tạo payment
        Payment payment = new Payment();
        payment.setOrders(orders);
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
        float newBalance = admin.getBalance() + orders.getTotal() * 0.10f;
        admin.setBalance(newBalance);
        setTransactions.add(transactions2);


        //Từ ADMIN tới CUSTOMER
        Transactions transactions3 = new Transactions();
        transactions3.setFrom(admin);
        Account owner = orders.getOrderDetails().get(0).getKoi().getAccount();
        transactions3.setTo(owner);
        transactions3.setPayment(payment);
        transactions3.setStatus(TransactionEnums.SUCCESS);
        transactions3.setDescription(" ADMIN TO OWNER");
        float newShopBalance = owner.getBalance() + orders.getTotal() * 0.90f;
        owner.setBalance(newShopBalance);
        setTransactions.add(transactions3);

        payment.setTransactions(setTransactions);
        accountRepository.save(admin);
        accountRepository.save(owner);
        paymentRepository.save(payment);
    }

    private OrderResponse convertToOrdersResponse(Orders orders) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderId(orders.getOrderId());
        orderResponse.setCustomerId(orders.getCustomer().getUserId());
        orderResponse.setTourId(orders.getTour().getTourId());
        orderResponse.setOrderDate(orders.getOrderDate());
        orderResponse.setDeliveredDate(orders.getDeliveredDate());
        orderResponse.setTotalPrice(orders.getTotalPrice());
        orderResponse.setStatus(orders.getStatus());
        orderResponse.setNote(orders.getNote());

        List<OrderDetailResponse> orderDetailResponses = orders.getOrderDetails().stream().map(orderDetail -> {
            OrderDetailResponse orderDetailResponse = new OrderDetailResponse();
            orderDetailResponse.setFarmId(orderDetail.getFarms().getFarmId());
            orderDetailResponse.setKoiId(orderDetail.getKoi().getKoiId());
            orderDetailResponse.setDescription(orderDetail.getDescription());
            orderDetailResponse.setQuantity(orderDetail.getQuantity());
            orderDetailResponse.setPrice(orderDetail.getPrice());

            return orderDetailResponse;
        }).collect(Collectors.toList());

        orderResponse.setOrderDetails(orderDetailResponses);

        return orderResponse;
    }
    
    
}

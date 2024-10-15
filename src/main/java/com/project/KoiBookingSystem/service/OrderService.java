package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.*;
import com.project.KoiBookingSystem.enums.PaymentEnums;
import com.project.KoiBookingSystem.enums.Role;
import com.project.KoiBookingSystem.enums.TransactionEnums;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.model.request.OrderDetailRequest;
import com.project.KoiBookingSystem.model.request.OrderRequest;
import com.project.KoiBookingSystem.repository.AccountRepository;
import com.project.KoiBookingSystem.repository.KoiRepository;
import com.project.KoiBookingSystem.repository.OrderRepository;
import com.project.KoiBookingSystem.repository.PaymentRepository;
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

@Service
public class OrderService {

    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    private KoiRepository koiRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public Orders createOrder(OrderRequest orderRequest) {
        Account customer = authenticationService.getCurrentAccount(); // lấy thằng vừa tạo order => customer
        Orders order = new Orders();
        List<OrderDetail> orderDetails = new ArrayList<>(); //
        float totalPrice = 0;
        order.setDate(new Date());
        order.setCustomer(customer);


        for(OrderDetailRequest orderDetailRequest : orderRequest.getDetail()){
//            Koi koi = koiRepository.findKoiByKoiID(String.valueOf(orderDetailRequest.getKoiID()));
            Koi koi = koiRepository.findKoiById(orderDetailRequest.getKoiID());
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setQuantity(orderDetailRequest.getQuantity());
            orderDetail.setPrice(koi.getPrice());
            orderDetail.setOrder(order);
            orderDetail.setKoi(koi);
            orderDetails.add(orderDetail);
            totalPrice += orderDetail.getPrice() * orderDetail.getQuantity();
        }
        order.setOrderDetails(orderDetails);
        order.setTotal(totalPrice);

        return orderRepository.save(order);
    }

    //lấy danh sách order
    public List<Orders> getAllOrders() {
        Account account = authenticationService.getCurrentAccount();
        List<Orders> ordersList = orderRepository.findOrderssByCustomer(account);
        return ordersList;
    }


    public String createUrl(OrderRequest ordersRequest) throws  Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime createDate = LocalDateTime.now();
        String formattedCreateDate = createDate.format(formatter);

        //code của mình
        Orders orders = createOrder(ordersRequest);
        float money = orders.getTotal() * 100;
        String amount = String.valueOf((int) money); // để mất thập phân kiểu int





        



        String tmnCode = "K8GYRSRJ";
        String secretKey = "GZRDJNWZ5DZCJF1PF3WV4MP7YX7ZT8H6";
        String vnpUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
        String returnUrl = "https://blearning.vn/guide/swp/docker-local?orderID=" + orders.getId();
        // trang thông báo thành công (font-end) làm
        // đính kèm Id cho orders (sản phẩm)
        String currCode = "VND";

        Map<String, String> vnpParams = new TreeMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", tmnCode);
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_CurrCode", currCode);
        vnpParams.put("vnp_TxnRef", orders.getId().toString()); //!!! Do Orders id đang là kiểu UUID (String)
        vnpParams.put("vnp_OrderInfo", "Thanh toan cho ma GD: " + orders.getId());
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
        Orders orders = orderRepository.findById(uuid)
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
    
    
}

package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.exception.PaymentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class VNPayService {

    @Autowired
    BookingService bookingService;

    private final String secretKey = "XJCDBBRSVGUXDDSHLLXMSGUCBHO9ZDL3"; // Nhập secretKey

    public String createPaymentUrl(String entityId, double amount, String orderType) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime createDate = LocalDateTime.now();
        String formattedCreateDate = createDate.format(formatter);

        String tmnCode = "97AA91OX"; // Nhập code tmn

        String vnpUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
        String returnUrl = "https://google.com/?id=" + entityId;
        String currCode = "VND";

        double price = amount * 100;
        String totalAmount = String.valueOf((int)price);

        Map<String, String> vnpParams = new TreeMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", tmnCode);
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_CurrCode", currCode);
        vnpParams.put("vnp_TxnRef", entityId);
        vnpParams.put("vnp_OrderInfo", "Thanh toan cho ma GD: " + entityId);
        vnpParams.put("vnp_OrderType", orderType);
        vnpParams.put("vnp_Amount", totalAmount);

        vnpParams.put("vnp_ReturnUrl", returnUrl);
        vnpParams.put("vnp_CreateDate", formattedCreateDate);
        vnpParams.put("vnp_IpAddr", "128.199.178.23");

        StringBuilder signDataBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            signDataBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
            signDataBuilder.append("=");
            signDataBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
            signDataBuilder.append("&");
        }
        signDataBuilder.deleteCharAt(signDataBuilder.length() - 1);

        String signData = signDataBuilder.toString();
        String signed = generateHMAC(secretKey, signData);

        vnpParams.put("vnp_SecureHash", signed);

        StringBuilder urlBuilder = new StringBuilder(vnpUrl);
        urlBuilder.append("?");
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            urlBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
            urlBuilder.append("=");
            urlBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
            urlBuilder.append("&");
        }
        urlBuilder.deleteCharAt(urlBuilder.length() - 1);

        return urlBuilder.toString();
    }

    public String generateHMAC(String secretKey, String signData) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSHA512 = Mac.getInstance("HmacSHA512");
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmacSHA512.init(keySpec);
        byte[] hmacBytes = hmacSHA512.doFinal(signData.getBytes(StandardCharsets.UTF_8));

        StringBuilder result = new StringBuilder();
        for (byte b : hmacBytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    private String buildSignData(Map<String, String> vnpParams) {
        StringBuilder signDataBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            if (!"vnp_SecureHash".equals(entry.getKey())) {
                signDataBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        signDataBuilder.deleteCharAt(signDataBuilder.length() - 1);
        return signDataBuilder.toString();
    }

//    public String handleBookingPaymentCallBack(Map<String, String> vnpParams) {
//        try {
//            String vnp_SecureHash = vnpParams.get("vnp_SecureHash");
//            String signData = buildSignData(vnpParams);
//            String expectedHash = generateHMAC(secretKey, signData);
//
//            if (!vnp_SecureHash.equals(expectedHash)) {
//                throw new PaymentException("Invalid secure hash!");
//            }
//
//            String bookingId = vnpParams.get("vnp_TxnRef");
//            String paymentStatus = vnpParams.get("vnp_TransactionStatus");
//
//            if ("00".equals(paymentStatus)) {
//                bookingService.createBookingTransaction(bookingId);
//                return "Payment Successfully!";
//            } else {
//                throw new PaymentException("Payment Failed! Transaction is not completed!");
//            }
//        } catch (NoSuchAlgorithmException e) {
//            throw new PaymentException("Invalid Payment!");
//        } catch (InvalidKeyException e) {
//            throw new PaymentException("Invalid Payment Key!");
//        }
//    }

}

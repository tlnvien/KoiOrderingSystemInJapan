package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.entity.Payment;
import com.project.KoiBookingSystem.entity.Tour;
import com.project.KoiBookingSystem.enums.PaymentStatus;
import com.project.KoiBookingSystem.enums.PaymentType;
import com.project.KoiBookingSystem.enums.Role;
import com.project.KoiBookingSystem.exception.ActionException;
import com.project.KoiBookingSystem.exception.AuthenticationException;
import com.project.KoiBookingSystem.exception.EmptyListException;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.model.response.PaymentResponse;
import com.project.KoiBookingSystem.repository.PaymentRepository;
import com.project.KoiBookingSystem.repository.TourRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    VNPayService vnPayService;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    TourRepository tourRepository;

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    public String initiatePayment(String id, String method, double price, String description, PaymentType paymentType) {
        Account customer = getValidAuthentication();
        if (price < 0) throw new IllegalArgumentException("Price can not be lower than 0!");
        if (method == null || method.isEmpty()) throw new IllegalArgumentException("Payment method is required!");

        Payment payment = new Payment();
        payment.setMethod(method);
        payment.setDescription(description);
        payment.setPrice(price);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCustomer(customer);
        payment.setPaymentType(paymentType);
        switch (paymentType) {
            case TOUR:
                Tour tour = tourRepository.findTourByTourId(id);
                if (tour == null) {
                    throw new NotFoundException("Tour with id " + id + " not found!");
                }
                payment.setTour(tour);
                break;
            case ORDER:
                break;
            case DELIVERING:
                break;
        }
        try {
            String paymentId = generatePaymentId();
            payment.setPaymentId(paymentId);
            paymentRepository.save(payment);
            logger.info("Payment initiated with id: {} ", paymentId);
        } catch (DataAccessException e) {
            throw new ActionException("Initiate Payment Failed!");
        }

        try {
            String paymentUrl = vnPayService.createPaymentUrl(payment.getPaymentId(), price);
            return paymentUrl;
        } catch (UnsupportedEncodingException e) {
            throw new ActionException("Failed to create Payment URL");
        }
    }

    public void completePayment(String paymentId, Map<String, String> queryParams) {
        Account customer = getValidAuthentication();
        boolean paymentSuccess = vnPayService.processPaymentResult(queryParams);
        Payment payment = paymentRepository.findByPaymentId(paymentId);
        if (payment == null) {
            throw new NotFoundException("Payment ID Not Found!");
        }
        if (paymentSuccess) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setPaymentDate(LocalDateTime.now());
            logger.info("Payment Id {} completed", paymentId);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            logger.info("Payment Id {} failed", paymentId);
        }
        paymentRepository.save(payment);
    }

    public void refundPayment(String paymentId) {
        Account customer = getValidAuthentication();
        Payment payment = paymentRepository.findByPaymentId(paymentId);
        if (payment == null || !payment.getStatus().equals(PaymentStatus.COMPLETED)) {
            throw new IllegalArgumentException("Payment can not be refunded!");
        }
        double refundPrice = payment.getPrice() * 0.5;

        payment.setStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);
        logger.info("Payment Id {} has been refunded: {}", paymentId, refundPrice);
    }

    public PaymentResponse getPaymentDetails(String paymentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId);
        return modelMapper.map(payment, PaymentResponse.class);
    }

    public void cancelPayment(String paymentId) {
        Account customer = getValidAuthentication();
        Payment payment = paymentRepository.findByPaymentId(paymentId);
        if (payment != null && payment.getStatus().equals(PaymentStatus.PENDING)) {
            payment.setStatus(PaymentStatus.CANCELLED);
            logger.info("Payment Id {} has been cancelled", paymentId);
            paymentRepository.save(payment);
        } else {
            throw new IllegalArgumentException("Payment can not be cancelled!");
        }
    }

    public List<PaymentResponse> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        if (payments.isEmpty()) throw new EmptyListException("List is empty!");
        return payments.stream().map(payment -> modelMapper.map(payment, PaymentResponse.class)).collect(Collectors.toList());
    }

    @Scheduled(fixedRate = 3600000) // 3600000 milliseconds = 1 hour
    public void checkPendingPayments() {
        List<Payment> pendingPayments = paymentRepository.findByStatus(PaymentStatus.PENDING);
        if (pendingPayments.isEmpty()) {
            logger.info("No pending payment to check!");
            return;
        }
        for (Payment payment : pendingPayments) {
            if (payment.getPaymentDate().plusHours(24).isBefore(LocalDateTime.now())) {
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
                logger.info("Payment Id {} failed since the time is out!", payment.getPaymentId());
            }
        }
    }

    public List<PaymentResponse> searchPayments(String paymentId, String customerId, PaymentStatus status) {
        List<Payment> payments = new ArrayList<>();
        if (paymentId != null && !paymentId.isEmpty()) {
            Payment payment = paymentRepository.findByPaymentId(paymentId);
            if (payment == null) {
                throw new NotFoundException("Payment Id Not Found!");
            }
            payments.add(payment);
        } else if (customerId != null && !customerId.isEmpty() && status != null) {
            payments = paymentRepository.findByCustomer_UserIdAndStatus(customerId, status);
        } else if (customerId != null && !customerId.isEmpty()) {
            payments = paymentRepository.findByCustomer_UserId(customerId);
        } else if (status != null) {
            payments = paymentRepository.findByStatus(status);
        } else {
            payments = paymentRepository.findAll();
        }

        return payments.stream().map(this::convertToPaymentResponse).collect(Collectors.toList());
    }

    public List<PaymentResponse> searchPaymentByCustomerRole(String paymentId, PaymentStatus status) {
        Account customer = getValidAuthentication();
        List<Payment> payments = new ArrayList<>();
        if (paymentId != null && !paymentId.isEmpty()) {
            Payment payment = paymentRepository.findByPaymentIdAndCustomer_UserId(paymentId, customer.getUserId());
            if (payment == null) throw new NotFoundException("Payment ID Not Found!");
            payments.add(payment);
        } else if (status != null) {
            payments = paymentRepository.findByCustomer_UserIdAndStatus(customer.getUserId(), status);
        } else {
            payments = paymentRepository.findByCustomer_UserId(customer.getUserId());
        }
        return payments.stream().map(this::convertToPaymentResponse).collect(Collectors.toList());
    }

    private PaymentResponse convertToPaymentResponse(Payment payment) {
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setPaymentId(payment.getPaymentId());
        paymentResponse.setCustomerId(payment.getCustomer().getUserId());
        paymentResponse.setMethod(payment.getMethod());
        paymentResponse.setDescription(payment.getDescription());
        paymentResponse.setPrice(payment.getPrice());
        paymentResponse.setPaymentDate(payment.getPaymentDate());
        paymentResponse.setPaymentType(payment.getPaymentType());
        paymentResponse.setStatus(payment.getStatus());

        return paymentResponse;
    }

    public Account getValidAuthentication() {
        Account customer = authenticationService.getCurrentAccount();
        if (customer == null || !customer.getRole().equals(Role.CUSTOMER)) {
            throw new AuthenticationException("Invalid Activity!");
        }
        return customer;
    }

    public String generatePaymentId() {
        Payment lastPayment = paymentRepository.findTopByOrderByIdDesc();
        int lastId = 0;
        if (lastPayment != null && lastPayment.getPaymentId() != null) {
            String lastRequestId = lastPayment.getPaymentId();
            lastId = Integer.parseInt(lastRequestId.substring(1));
        }

        return "PM" + (lastId + 1);
    }
}

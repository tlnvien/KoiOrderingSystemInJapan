package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.Payment;
import com.project.KoiBookingSystem.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    PaymentRepository paymentRepository;

    public String generatePaymentId() {
        Payment lastPayment = paymentRepository.findTopByOrderByIdDesc();
        int lastId = 0;
        if (lastPayment != null && lastPayment.getPaymentId() != null) {
            String lastPaymentId = lastPayment.getPaymentId();
            lastId = Integer.parseInt(lastPaymentId.substring(2));
        }

        return "PM" + (lastId + 1);
    }


}

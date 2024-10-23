package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.model.response.EmailDetail;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    JavaMailSender javaMailSender;

    public void sendWelcomeEmail(EmailDetail emailDetail) {
        try {
            Context context = new Context();
            context.setVariable("name", emailDetail.getAccount().getUsername());
            context.setVariable("button", "Go to Home Page!");
            context.setVariable("link", emailDetail.getLink());

            String template = templateEngine.process("welcome-user", context);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            mimeMessageHelper.setFrom("tranlenhat123456@gmail.com");
            mimeMessageHelper.setTo(emailDetail.getAccount().getEmail());
            mimeMessageHelper.setText(template, true);
            mimeMessageHelper.setSubject(emailDetail.getSubject());
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new MailSendException("Send welcome mail failed!");
        }
    }

    public void sendForgotPasswordEmail(EmailDetail emailDetail) {
        try {
            Context context = new Context();
            context.setVariable("name", emailDetail.getAccount().getUsername());
            context.setVariable("link", emailDetail.getLink());
            context.setVariable("email", emailDetail.getAccount().getEmail());
            context.setVariable("resetCode", emailDetail.getCode());

            String template = templateEngine.process("forgot-password", context);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            mimeMessageHelper.setFrom("tranlenhat123456@gmail.com");
            mimeMessageHelper.setTo(emailDetail.getAccount().getEmail());
            mimeMessageHelper.setText(template, true);
            mimeMessageHelper.setSubject(emailDetail.getSubject());
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new MailSendException("Send forgot password mail failed!");
        }
    }

    public void sendRegistrationCodeEmail(EmailDetail emailDetail) {
        try {
            Context context = new Context();
            context.setVariable("name", emailDetail.getAccount().getUsername());
            context.setVariable("registrationCode", emailDetail.getCode());

            String template = templateEngine.process("registration-code", context);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            mimeMessageHelper.setFrom("tranlenhat123456@gmail.com");
            mimeMessageHelper.setTo(emailDetail.getAccount().getEmail());
            mimeMessageHelper.setText(template, true);
            mimeMessageHelper.setSubject(emailDetail.getSubject());
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new MailSendException("Send registration code mail failed!");
        }
    }

    public void sendBookingCompleteEmail(EmailDetail emailDetail) {
        try {
            Context context = new Context();
            context.setVariable("customerName", emailDetail.getAccount().getUsername());
            context.setVariable("bookingId", emailDetail.getBooking().getBookingId());
            context.setVariable("tourName", emailDetail.getBooking().getTour().getTourName());
            context.setVariable("departureDate", emailDetail.getBooking().getTour().getDepartureDate());
            context.setVariable("numberOfAttendees", emailDetail.getBooking().getNumberOfAttendances());
            context.setVariable("totalPrice", emailDetail.getBooking().getTotalPrice());
            context.setVariable("link", emailDetail.getLink());

            String template = templateEngine.process("booking-confirmation", context);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            mimeMessageHelper.setFrom("tranlenhat123456@gmail.com");
            mimeMessageHelper.setTo(emailDetail.getAccount().getEmail());
            mimeMessageHelper.setText(template, true);
            mimeMessageHelper.setSubject(emailDetail.getSubject());
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new MailSendException("Send booking confirmation mail failed!");
        }
    }

    public void sendBookingPaymentEmail(EmailDetail emailDetail) {
        try {
            Context context = new Context();
            context.setVariable("customerName", emailDetail.getAccount().getUsername());
            context.setVariable("bookingId", emailDetail.getBooking().getBookingId());
            context.setVariable("tourName", emailDetail.getBooking().getTour().getTourName());
            context.setVariable("link", emailDetail.getLink());

            String template = templateEngine.process("booking-payment", context);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            mimeMessageHelper.setFrom("tranlenhat123456@gmail.com");
            mimeMessageHelper.setTo(emailDetail.getAccount().getEmail());
            mimeMessageHelper.setText(template, true);
            mimeMessageHelper.setSubject(emailDetail.getSubject());
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new MailSendException("Send booking payment mail failed!");
        }
    }

    public void sendOrderCompleteEmail(EmailDetail emailDetail) {
        try {
            Context context = new Context();
            context.setVariable("customerName", emailDetail.getAccount().getUsername());
            context.setVariable("orderId", emailDetail.getOrder().getOrderId());
            context.setVariable("orderDate", emailDetail.getOrder().getOrderDate());
            context.setVariable("paidPrice", emailDetail.getOrder().getPaidPrice());
            context.setVariable("orderItems", emailDetail.getOrder().getOrderDetails());
            context.setVariable("link", emailDetail.getLink());

            String template = templateEngine.process("order-confirmation", context);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            mimeMessageHelper.setFrom("tranlenhat123456@gmail.com");
            mimeMessageHelper.setTo(emailDetail.getAccount().getEmail());
            mimeMessageHelper.setText(template, true);
            mimeMessageHelper.setSubject(emailDetail.getSubject());
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new MailSendException("Send order confirmation mail failed!");
        }
    }

    public void sendOrderDeliveredSuccessfully(EmailDetail emailDetail) {
        try {
            Context context = new Context();
            context.setVariable("deliverId", emailDetail.getOrder().getDelivering().getDeliveringId());
            context.setVariable("orderId", emailDetail.getOrder().getOrderId());
            context.setVariable("deliveredDate", emailDetail.getOrder().getDeliveredDate());
            context.setVariable("remainingPrice", emailDetail.getOrder().getTotalPrice());
            context.setVariable("orderItems", emailDetail.getOrder().getOrderDetails());
            context.setVariable("link", emailDetail.getLink());

            String template = templateEngine.process("delivering-successfully", context);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            mimeMessageHelper.setFrom("tranlenhat123456@gmail.com");
            mimeMessageHelper.setTo(emailDetail.getAccount().getEmail());
            mimeMessageHelper.setText(template, true);
            mimeMessageHelper.setSubject(emailDetail.getSubject());
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new MailSendException("Send order delivered successfully mail failed!");
        }
    }
}

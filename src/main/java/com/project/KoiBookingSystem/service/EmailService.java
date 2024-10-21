package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.model.response.EmailDetail;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
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

    public void sendEmail(EmailDetail emailDetail) {
        try {
            Context context = new Context();
            context.setVariable("name", emailDetail.getAccount().getUsername());
            context.setVariable("button", "Go to Home Page!");
            context.setVariable("link", emailDetail.getLink());

            String template = templateEngine.process("register-template", context);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            mimeMessageHelper.setFrom("admin@gmail.com");
            mimeMessageHelper.setTo(emailDetail.getAccount().getEmail());
            mimeMessageHelper.setText(template, true);
            mimeMessageHelper.setSubject(emailDetail.getSubject());
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            System.out.println("Send mail failed!");
        }
    }

    public void sendBookingCompleteEmail(EmailDetail emailDetail) {
        try {
            Context context = new Context();
            context.setVariable("customerName", emailDetail.getAccount().getUsername());
            context.setVariable("bookingId", emailDetail.getBooking().getBookingID());
            context.setVariable("tourName", emailDetail.getBooking().getTourId().getTourName());
            context.setVariable("numberOfAttendees", emailDetail.getBooking().getNumberOfPerson());
            context.setVariable("totalPrice", emailDetail.getBooking().getTotalPrice());
            context.setVariable("link", emailDetail.getLink());

            // Xử lý mẫu HTML với Thymeleaf
            String template = templateEngine.process("booking-confirmation", context);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true); // true cho phép gửi email HTML

            mimeMessageHelper.setFrom("vietle282004@gmail.com");
            mimeMessageHelper.setTo(emailDetail.getAccount().getEmail());
            mimeMessageHelper.setText(template, true);
            mimeMessageHelper.setSubject(emailDetail.getSubject());

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            System.out.println("Gửi email thất bại: " + e.getMessage());
        }
    }
}


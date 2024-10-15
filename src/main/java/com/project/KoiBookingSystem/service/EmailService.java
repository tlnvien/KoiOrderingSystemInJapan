package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.model.response.EmailDetail;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
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
}

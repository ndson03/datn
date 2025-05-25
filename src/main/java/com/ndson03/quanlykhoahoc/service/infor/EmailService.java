package com.ndson03.quanlykhoahoc.service.infor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendPasswordResetEmail(String to, String token, String fullName) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("noreply@quanlykhoahoc.com");
        helper.setTo(to);
        helper.setSubject("Yêu cầu đặt lại mật khẩu");

        Context context = new Context();
        context.setVariable("token", token);
        context.setVariable("fullName", fullName);
        context.setVariable("resetUrl", "https://ndson.online/reset-password?token=" + token);

        String emailContent = templateEngine.process("reset-password-email", context);
        helper.setText(emailContent, true);

        mailSender.send(message);
    }
}
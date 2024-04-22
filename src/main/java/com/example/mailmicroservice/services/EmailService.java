package com.example.mailmicroservice.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private TemplateEngine templateEngine;

    public void sendEmail(String to) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject("subject");
            helper.setText("body", true);
            helper.setFrom("avelicea2@gmail.com", "UNTOLD");
            javaMailSender.send(message);
        } catch (MessagingException e) {
            handleMessagingException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendHtmlEmailRegister(String to, String subject, String body) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            String defaultFromAddress = "avelicea2@gmail.com";
            helper.setFrom(defaultFromAddress, "UNTOLD");
            String htmlContent = generateHtmlContent(to,subject,body);
            helper.setText(htmlContent, true);
            javaMailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            handleMessagingException((MessagingException) e);
        }
    }

    private String generateHtmlContent(String name, String subject, String body) {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("body", body);
        context.setVariable("subject", subject);
        return templateEngine.process("SendedMail", context);
    }

    private void handleMessagingException(MessagingException e) {
        e.printStackTrace();
    }
}


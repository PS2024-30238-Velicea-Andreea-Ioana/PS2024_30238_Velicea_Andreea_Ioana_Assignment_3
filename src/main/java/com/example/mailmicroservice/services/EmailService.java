package com.example.mailmicroservice.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
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

    public void sendHtmlEmailRegisterWithAttachment(String to, String subject, String body, byte[] attachment, String attachmentFileName, String attachmentType) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            String defaultFromAddress = "avelicea2@gmail.com";
            helper.setFrom(defaultFromAddress, "UNTOLD");
            String htmlContent = generateHtmlContent1(to, subject, body);
            helper.setText(htmlContent, true);
            if ("pdf".equalsIgnoreCase(attachmentType)) {
                helper.addAttachment(attachmentFileName, new ByteArrayResource(attachment), "application/pdf");
            } else if ("txt".equalsIgnoreCase(attachmentType)) {
                helper.addAttachment(attachmentFileName, new ByteArrayResource(attachment), "text/plain");
            } else if ("csv".equalsIgnoreCase(attachmentType)) {
                helper.addAttachment(attachmentFileName, new ByteArrayResource(attachment), "text/csv");
            } else {
                throw new IllegalArgumentException("Unsupported attachment type: " + attachmentType);
            }

            javaMailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            handleMessagingException((MessagingException) e);
        }
    }

    private String generateHtmlContent1(String name, String subject, String body) {
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


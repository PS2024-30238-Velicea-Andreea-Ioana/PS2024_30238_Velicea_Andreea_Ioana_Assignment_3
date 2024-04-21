package com.example.mailmicroservice;

import com.example.mailmicroservice.services.EmailService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

public class MailMicroserviceApplication {
    private EmailService emailService;

    public MailMicroserviceApplication(EmailService emailService) {
        this.emailService = emailService;
    }

    public static void main(String[] args) {
        SpringApplication.run(MailMicroserviceApplication.class, args);
    }
}

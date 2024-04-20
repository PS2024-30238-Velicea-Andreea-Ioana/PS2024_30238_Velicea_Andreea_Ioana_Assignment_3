package com.example.mailmicroservice;

import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
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
//        MailMicroserviceApplication app = applicationContext.getBean(MailMicroserviceApplication.class);
//        app.run(); // Sending test email
    }

//    private void run() {
//        try {
//            emailService.sendTestEmail();
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

}

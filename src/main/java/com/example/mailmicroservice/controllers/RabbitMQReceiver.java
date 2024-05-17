package com.example.mailmicroservice.controllers;

import com.example.mailmicroservice.entities.MessageDto;
import com.example.mailmicroservice.entities.NotificationRequestDto;
import com.example.mailmicroservice.services.EmailService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


@Component
@RestController
@RequestMapping(value = "/receiver")
public class RabbitMQReceiver {
    private static final Logger logger = LogManager.getLogger(RabbitMQReceiver.class);
    private final EmailService emailService;

    @Autowired
    public RabbitMQReceiver(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping(value = "/sendEmail")
    public ResponseEntity<MessageDto> receiveAndSendEmail(@RequestBody NotificationRequestDto notificationRequestDto, @RequestHeader HttpHeaders httpHeaders) {
        try {
            logger.info("Received NotificationRequestDto: {}", notificationRequestDto);
            logger.info("Received HttpHeaders: {}", httpHeaders);

            if (isValidToken(httpHeaders, notificationRequestDto)) {
                String email = notificationRequestDto.getEmail();

                if(notificationRequestDto.getAction().equals("register")) {
                    emailService.sendHtmlEmailRegister(email, "Registration", "Thank you for registering with us. Your account has been successfully created.\n You can now log in using the credentials you provided during registration. \n If you have any questions or need further assistance, feel free to contact us.");
                    logger.info("Email sent successfully to " + email);
                }else if(notificationRequestDto.getAction().equals("order")){
                    emailService.sendHtmlEmailRegister(email,"Order", "Thank you for your order. Your order has been confirmed! \n" +
                            "We appreciate your business and look forward to serving you again.\n");
                    logger.info("Email sent successfully to " + email);
                }else if (notificationRequestDto.getAction().equals("adminFile")){
                    byte[] attachmentContent = readFile(notificationRequestDto.getFilePath());

                    // Determine attachment type based on file extension
                    String attachmentType = getAttachmentType(notificationRequestDto.getFilePath());

                    // Send email with attachment
                    emailService.sendHtmlEmailRegisterWithAttachment(email, "File Attachment", "Please find the attached file.", attachmentContent, "attachment." + attachmentType, attachmentType);
                    logger.info("Email with attachment sent successfully to " + email);
                }
                MessageDto messageDto = new MessageDto();
                messageDto.setStatus("Success");
                messageDto.setMessage("Email sent successfully");

                return ResponseEntity.ok(messageDto);
            } else {
                logger.error("Unauthorized access. Invalid token.");
                MessageDto messageDto = new MessageDto();
                messageDto.setStatus("Error");
                messageDto.setMessage("Unauthorized access. Invalid token.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(messageDto);
            }
        } catch (Exception e) {
            logger.error("Error sending email: " + e.getMessage());
            MessageDto messageDto = new MessageDto();
            messageDto.setStatus("Error");
            messageDto.setMessage("Error sending email");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(messageDto);
        }
    }

    private byte[] readFile(String filePath) throws IOException {
        return Files.readAllBytes(Paths.get(filePath));
    }
    private boolean isValidToken(HttpHeaders httpHeaders, NotificationRequestDto notificationRequestDto) {
        String autorizationHeader = httpHeaders.getFirst(HttpHeaders.AUTHORIZATION);
        if (notificationRequestDto == null) return false;
        if (autorizationHeader != null && autorizationHeader.startsWith("Bearer ")) {
            String token = autorizationHeader.substring(7);
            return token.equals(notificationRequestDto.getId() + notificationRequestDto.getId());
        }
        return false;
    }
    private String getAttachmentType(String filePath) {
        String[] parts = filePath.split("\\.");
        if (parts.length > 0) {
            return parts[parts.length - 1];
        }
        return "txt"; // Default to txt if file extension not found
    }
}
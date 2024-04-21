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
            // Log received data for debugging
            logger.info("Received NotificationRequestDto: {}", notificationRequestDto);
            logger.info("Received HttpHeaders: {}", httpHeaders);

            if (isValidToken(httpHeaders, notificationRequestDto)) {
                // Extract email from NotificationRequestDto
                String email = notificationRequestDto.getEmail();

                if(notificationRequestDto.getAction().equals("register")) {
                    emailService.sendHtmlEmailRegister(email, "Registration", "Thank you for registering with us. Your account has been successfully created.\n You can now log in using the credentials you provided during registration. \n If you have any questions or need further assistance, feel free to contact us.");
                    logger.info("Email sent successfully to " + email);
                }else if(notificationRequestDto.getAction().equals("order")){
                    emailService.sendHtmlEmailRegister(email,"Order", "Thank you for your order. Your order has been confirmed! \n" +
                            "We appreciate your business and look forward to serving you again.\n");
                    logger.info("Email sent successfully to " + email);
                }
                // Construct success message
                MessageDto messageDto = new MessageDto();
                messageDto.setStatus("Success");
                messageDto.setMessage("Email sent successfully");

                // Return success response
                return ResponseEntity.ok(messageDto);
            } else {
                logger.error("Unauthorized access. Invalid token.");
                // Construct unauthorized access message
                MessageDto messageDto = new MessageDto();
                messageDto.setStatus("Error");
                messageDto.setMessage("Unauthorized access. Invalid token.");

                // Return unauthorized access response
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(messageDto);
            }
        } catch (Exception e) {
            logger.error("Error sending email: " + e.getMessage());
            // Construct error message
            MessageDto messageDto = new MessageDto();
            messageDto.setStatus("Error");
            messageDto.setMessage("Error sending email");

            // Return error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(messageDto);
        }
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
}
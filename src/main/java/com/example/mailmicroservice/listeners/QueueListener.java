package com.example.mailmicroservice.listeners;

import com.example.mailmicroservice.services.EmailService;
import com.example.untoldpsproject.dtos.Payload;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class QueueListener {
    private final EmailService emailService;

    public QueueListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = "${rabbitmq.queue}")
    public void listen(Payload payload) {
        try {
            emailService.sendHtmlEmailRegister(payload.getEmail(),"Order", "Thank you for your order. Your order has been confirmed!\n" +
                    "We appreciate your business and look forward to serving you again. \n");
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        System.out.println("Message read from myQueue: " + payload.toString());
    }
}

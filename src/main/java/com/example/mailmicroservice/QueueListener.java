package com.example.mailmicroservice;

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
            emailService.sendEmail(payload.getEmail(), "fromqueue", "message from queue");
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        System.out.println("Message read from myQueue: " + payload.toString());
    }
}

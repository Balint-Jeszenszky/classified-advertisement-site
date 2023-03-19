package hu.bme.aut.classifiedadvertisementsite.userservice.service.messagequeue;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class NotificationMessageQueue {
    private static final String QUEUE_NAME = "email-queue";

    @Bean
    Queue queue() {
        return new Queue(QUEUE_NAME, true);
    }
}

package hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.service.messagequeue;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ImageProcessingQueue {
    private static final String QUEUE_NAME = "image-processing-queue";

    @Bean
    Queue queue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    MessageListenerAdapter listenerAdapter(ImageProcessingMessageReceiver receiver) {
        return new MessageListenerAdapter(receiver);
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(QUEUE_NAME);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setMessageListener(listenerAdapter);
        return container;
    }
}

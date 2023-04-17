package hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.service.messagequeue;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ImageProcessingMessageReceiver implements ChannelAwareMessageListener {

    @Override
    public void onMessage(Message message, Channel channel) throws IOException {
        System.out.println("Received <" + new String(message.getBody()) + ">");
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}

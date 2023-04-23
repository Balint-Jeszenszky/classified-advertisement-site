package hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.service.messagequeue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.service.ImageProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImageProcessingMessageReceiver implements ChannelAwareMessageListener {
    private final ImageProcessingService imageProcessingService;

    @Override
    public void onMessage(Message message, Channel channel) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(message.getBody());

            switch (node.get("type").asText()) {
                case "PROCESS":
                    imageProcessingService.processImage(node.get("name").asText(), node.get("advertisementId").asInt());
                    break;
                case "DELETE":
                    List<Integer> imageIds = new ArrayList<>();
                    if (node.get("imageIds").isArray()) {
                        for (final JsonNode objNode : node.get("imageIds")) {
                            imageIds.add(objNode.asInt());
                        }
                    }
                    imageProcessingService.deleteImagesById(imageIds);
                    break;
                case "DELETE_AD":
                    imageProcessingService.deleteImagesByAdvertisementId(node.get("advertisementId").asInt());
                    break;
                default:
                    channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                    return;
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error(e.getMessage());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }
}

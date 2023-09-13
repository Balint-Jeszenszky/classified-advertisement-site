package hu.bme.aut.classifiedadvertisementsite.userservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Profile("!test")
public class NotificationServiceImpl implements NotificationService {
    private final RabbitTemplate rabbitTemplate;
    private final Queue queue;

    public void sendEmail(String email, String templateName, ObjectNode data) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("toAddress", email);
        node.put("template", templateName);
        node.set("data", data);
        rabbitTemplate.convertAndSend(queue.getName(), node.toString());
    }
}

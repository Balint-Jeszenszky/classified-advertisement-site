package hu.bme.aut.classifiedadvertisementsite.userservice.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("test")
public class MockNotificationService implements NotificationService {

    @Override
    public void sendEmail(String email, String templateName, ObjectNode data) {

    }
}

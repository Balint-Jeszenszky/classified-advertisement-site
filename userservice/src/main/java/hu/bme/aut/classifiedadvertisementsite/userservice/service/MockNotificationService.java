package hu.bme.aut.classifiedadvertisementsite.userservice.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("test")
public class MockNotificationService implements NotificationService {

    public void sendEmail(String email, String subject, String content) {

    }
}

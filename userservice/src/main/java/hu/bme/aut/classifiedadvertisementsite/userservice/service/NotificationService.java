package hu.bme.aut.classifiedadvertisementsite.userservice.service;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface NotificationService {
    void sendEmail(String email, String templateName, ObjectNode data);
}

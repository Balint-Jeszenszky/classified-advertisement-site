package hu.bme.aut.classifiedadvertisementsite.userservice.service;

public interface NotificationService {
    void sendEmail(String email, String subject, String content);
}

package hu.bme.aut.classifiedadvertisementsite.userservice.service;

import hu.bme.aut.classifiedadvertisementsite.userservice.controller.exceptions.NotFoundException;
import hu.bme.aut.classifiedadvertisementsite.userservice.model.EmailVerification;
import hu.bme.aut.classifiedadvertisementsite.userservice.model.User;
import hu.bme.aut.classifiedadvertisementsite.userservice.repository.EmailVerificationRepository;
import hu.bme.aut.classifiedadvertisementsite.userservice.repository.UserRepository;
import hu.bme.aut.classifiedadvertisementsite.userservice.service.util.RandomStringGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

    private final EmailVerificationRepository emailVerificationRepository;
    private final UserRepository userRepository;

    public void sendVerificationEmail(User user, String email) {
        String key = RandomStringGenerator.generate(32);

        EmailVerification emailVerification = EmailVerification.builder()
                .email(email)
                .user(user)
                .key(key)
                .build();

        emailVerificationRepository.save(emailVerification);

        log.info("Sending verification email to {}, key: {}", email, key); // TODO use the notification microservice
    }

    public void validateKey(String key) {
        EmailVerification emailVerification = emailVerificationRepository.getEmailVerificationByKey(key)
                .orElseThrow(() -> new NotFoundException("Invalid key"));

        User user = emailVerification.getUser();

        user.setEmail(emailVerification.getEmail());
        userRepository.save(user);

        emailVerificationRepository.delete(emailVerification);
    }
}

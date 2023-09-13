package hu.bme.aut.classifiedadvertisementsite.userservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import hu.bme.aut.classifiedadvertisementsite.userservice.controller.exceptions.NotFoundException;
import hu.bme.aut.classifiedadvertisementsite.userservice.model.EmailVerification;
import hu.bme.aut.classifiedadvertisementsite.userservice.model.User;
import hu.bme.aut.classifiedadvertisementsite.userservice.repository.EmailVerificationRepository;
import hu.bme.aut.classifiedadvertisementsite.userservice.repository.UserRepository;
import hu.bme.aut.classifiedadvertisementsite.userservice.service.util.RandomStringGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

    private final EmailVerificationRepository emailVerificationRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public void sendVerificationEmail(User user, String email) {
        String key = RandomStringGenerator.generate(32);

        EmailVerification emailVerification = EmailVerification.builder()
                .email(email)
                .user(user)
                .key(key)
                .build();

        emailVerificationRepository.deleteAllByUser(user);
        emailVerificationRepository.flush();
        emailVerificationRepository.save(emailVerification);

        log.info("Sending verification email to {}, key: {}", email, key);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("user", user.getUsername());
        node.put("code", key);

        notificationService.sendEmail(email, "emailVerification", node);
    }

    public void validateKey(String key) {
        EmailVerification emailVerification = emailVerificationRepository.getEmailVerificationByKey(key)
                .orElseThrow(() -> new NotFoundException("Invalid key"));

        User user = emailVerification.getUser();

        user.setEmail(emailVerification.getEmail());
        userRepository.save(user);

        log.info("email {} verified for user id({})", emailVerification.getEmail(), user.getId());

        emailVerificationRepository.delete(emailVerification);
    }
}

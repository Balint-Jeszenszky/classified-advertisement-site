package hu.bme.aut.classifiedadvertisementsite.userservice.unit.service;

import hu.bme.aut.classifiedadvertisementsite.userservice.controller.exceptions.NotFoundException;
import hu.bme.aut.classifiedadvertisementsite.userservice.model.EmailVerification;
import hu.bme.aut.classifiedadvertisementsite.userservice.model.User;
import hu.bme.aut.classifiedadvertisementsite.userservice.repository.EmailVerificationRepository;
import hu.bme.aut.classifiedadvertisementsite.userservice.repository.UserRepository;
import hu.bme.aut.classifiedadvertisementsite.userservice.service.EmailVerificationService;
import hu.bme.aut.classifiedadvertisementsite.userservice.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class EmailVerificationServiceTest {
    private EmailVerificationRepository emailVerificationRepository;
    private UserRepository userRepository;
    private NotificationService notificationService;
    private EmailVerificationService emailVerificationService;

    @BeforeEach
    public void init() {
        userRepository = mock(UserRepository.class);
        notificationService = mock(NotificationService.class);
        emailVerificationRepository = mock(EmailVerificationRepository.class);
        emailVerificationService = new EmailVerificationService(
                emailVerificationRepository,
                userRepository,
                notificationService);
    }

    @Test
    void emailVerificationFailsWithInvalidKey() {
        String key = "invalidKey";
        when(emailVerificationRepository.getEmailVerificationByKey(key)).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> emailVerificationService.validateKey(key));

        verify(emailVerificationRepository, times(1)).getEmailVerificationByKey(key);
        verify(userRepository, never()).save(any(User.class));
        verify(emailVerificationRepository, never()).delete(any(EmailVerification.class));
    }

    @Test
    void emailVerificationSuccessfulWithValidKey() {
        String key = "validKey";
        User user = User.builder().id(1).build();
        EmailVerification verification = new EmailVerification(1, "email", user, key);
        when(emailVerificationRepository.getEmailVerificationByKey(key)).thenReturn(
                Optional.of(verification)
        );

        emailVerificationService.validateKey(key);

        verify(emailVerificationRepository, times(1)).getEmailVerificationByKey(key);
        verify(userRepository, times(1)).save(user);
        verify(emailVerificationRepository, times(1)).delete(verification);
    }
}

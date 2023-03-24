package hu.bme.aut.classifiedadvertisementsite.userservice.unit.service;

import hu.bme.aut.classifiedadvertisementsite.userservice.api.external.model.RegistrationRequest;
import hu.bme.aut.classifiedadvertisementsite.userservice.controller.exceptions.BadRequestException;
import hu.bme.aut.classifiedadvertisementsite.userservice.model.ERole;
import hu.bme.aut.classifiedadvertisementsite.userservice.model.Role;
import hu.bme.aut.classifiedadvertisementsite.userservice.model.User;
import hu.bme.aut.classifiedadvertisementsite.userservice.repository.PasswordResetRepository;
import hu.bme.aut.classifiedadvertisementsite.userservice.repository.RoleRepository;
import hu.bme.aut.classifiedadvertisementsite.userservice.repository.UserRepository;
import hu.bme.aut.classifiedadvertisementsite.userservice.service.AuthService;
import hu.bme.aut.classifiedadvertisementsite.userservice.service.EmailVerificationService;
import hu.bme.aut.classifiedadvertisementsite.userservice.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AuthServiceTests {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private EmailVerificationService emailVerificationService;
    private PasswordResetRepository passwordResetRepository;
    private NotificationService notificationService;
    private AuthService authService;

    @BeforeEach
    public void init() {
        userRepository = mock(UserRepository.class);
        roleRepository = mock(RoleRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        authenticationManager = mock(AuthenticationManager.class);
        emailVerificationService = mock(EmailVerificationService.class);
        passwordResetRepository = mock(PasswordResetRepository.class);
        notificationService = mock(NotificationService.class);
        authService = new AuthService(
                userRepository,
                roleRepository,
                passwordEncoder,
                authenticationManager,
                emailVerificationService,
                passwordResetRepository,
                notificationService);
    }

    @Test
    void registerUserFailsWithInvalidUsername() {
        RegistrationRequest invalidUser = getValidRegistrationRequest().username("/*+!%/')");

        Exception exception = assertThrows(
                BadRequestException.class,
                () -> authService.registerUser(invalidUser));
        assertEquals(exception.getClass(), BadRequestException.class);
    }

    @Test
    void registerUserFailsWithInvalidEmail() {
        RegistrationRequest invalidUser = getValidRegistrationRequest().email("invalid");

        Exception exception = assertThrows(
                BadRequestException.class,
                () -> authService.registerUser(invalidUser));
        assertEquals(exception.getClass(), BadRequestException.class);
    }

    @Test
    void registerUserFailsWithExistingEmail() {
        RegistrationRequest existingUser = getValidRegistrationRequest();
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        Exception exception = assertThrows(
                BadRequestException.class,
                () -> authService.registerUser(existingUser));
        assertEquals(exception.getClass(), BadRequestException.class);
    }

    @Test
     void registerUserFailsWithExistingUsername() {
        RegistrationRequest existingUser = getValidRegistrationRequest();
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        Exception exception = assertThrows(
                BadRequestException.class,
                () -> authService.registerUser(existingUser));
        assertEquals(exception.getClass(), BadRequestException.class);
    }

    @Test
    void registerUserFailsWithInvalidPassword() {
        String password = "p";
        RegistrationRequest invalidUser = getValidRegistrationRequest().password(password).confirmPassword(password);

        Exception exception = assertThrows(
                BadRequestException.class,
                () -> authService.registerUser(invalidUser));
        assertEquals(exception.getClass(), BadRequestException.class);
    }

    @Test
    void registerUserFailsWithNotMatchingPassword() {
        RegistrationRequest invalidUser = getValidRegistrationRequest().password("password").confirmPassword("password2");

        Exception exception = assertThrows(
                BadRequestException.class,
                () -> authService.registerUser(invalidUser));
        assertEquals(exception.getClass(), BadRequestException.class);
    }

    @Test
    void registerUserCreatesRolesWhenNoRolesExists() {
        RegistrationRequest admin = getValidRegistrationRequest();
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.empty(), Optional.of(new Role()));
        when(roleRepository.findByName(ERole.ROLE_ADMIN)).thenReturn(Optional.of(new Role()));

        authService.registerUser(admin);

        verify(roleRepository).saveAllAndFlush(List.of(
                Role.builder().name(ERole.ROLE_ADMIN).build(),
                Role.builder().name(ERole.ROLE_USER).build()));
        verify(roleRepository, times(1)).findByName(ERole.ROLE_ADMIN);
        verify(roleRepository, times(2)).findByName(ERole.ROLE_USER);
    }

    @Test
    void registerUserWhenRolesExists() {
        RegistrationRequest user = getValidRegistrationRequest();
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(new Role()));

        authService.registerUser(user);

        verify(roleRepository, never()).findByName(ERole.ROLE_ADMIN);
        verify(roleRepository, times(2)).findByName(ERole.ROLE_USER);
    }

    @Test
    void registerUserSendsVerificationEmail() {
        String encodedPassword = "ENCODED_PASSWORD";
        Role userRole = new Role(2, ERole.ROLE_USER);
        RegistrationRequest user = getValidRegistrationRequest();
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(user.getPassword())).thenReturn(encodedPassword);

        authService.registerUser(user);

        verify(emailVerificationService, times(1))
                .sendVerificationEmail(new User(
                        null,
                        user.getUsername(),
                        null,
                        encodedPassword,
                        true,
                        Set.of(userRole)), user.getEmail());
    }

    private RegistrationRequest getValidRegistrationRequest() {
        return new RegistrationRequest()
                .email("test@test.test")
                .username("user")
                .password("password")
                .confirmPassword("password");
    }
}

package hu.bme.aut.classifiedadvertisementsite.userservice.unit.service;

import hu.bme.aut.classifiedadvertisementsite.userservice.api.external.model.ModifyUserRequest;
import hu.bme.aut.classifiedadvertisementsite.userservice.controller.exceptions.BadRequestException;
import hu.bme.aut.classifiedadvertisementsite.userservice.model.Role;
import hu.bme.aut.classifiedadvertisementsite.userservice.model.User;
import hu.bme.aut.classifiedadvertisementsite.userservice.repository.RoleRepository;
import hu.bme.aut.classifiedadvertisementsite.userservice.repository.UserRepository;
import hu.bme.aut.classifiedadvertisementsite.userservice.security.LoggedInUserService;
import hu.bme.aut.classifiedadvertisementsite.userservice.service.EmailVerificationService;
import hu.bme.aut.classifiedadvertisementsite.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;

import static hu.bme.aut.classifiedadvertisementsite.userservice.model.ERole.ROLE_ADMIN;
import static hu.bme.aut.classifiedadvertisementsite.userservice.model.ERole.ROLE_USER;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private LoggedInUserService loggedInUserService;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private RoleRepository roleRepository;
    private EmailVerificationService emailVerificationService;
    private UserService userService;

    @BeforeEach
    public void init() {
        loggedInUserService = mock(LoggedInUserService.class);
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        roleRepository  = mock(RoleRepository.class);
        emailVerificationService = mock(EmailVerificationService.class);
        userService = new UserService(
                loggedInUserService,
                userRepository,
                passwordEncoder,
                roleRepository,
                emailVerificationService);
    }

    @Test
    void adminCannotRemoveTheirOwnAdminRole() {
        User admin = new User(
                1,
                "admin",
                "email@test.test",
                null,
                true,
                Set.of(new Role(1, ROLE_ADMIN), new Role(2, ROLE_USER)));
        when(loggedInUserService.getLoggedInUser()).thenReturn(admin);

        assertThrows(
                BadRequestException.class,
                () -> userService.modifyUser(
                        1,
                        new ModifyUserRequest().enabled(true).email("email@test.test").roles(List.of(ROLE_USER.name()))));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void adminCannotDeactivateTheirOwnUser() {
        User admin = new User(
                1,
                "admin",
                "email@test.test",
                null,
                true,
                Set.of(new Role(1, ROLE_ADMIN), new Role(2, ROLE_USER)));
        when(loggedInUserService.getLoggedInUser()).thenReturn(admin);

        assertThrows(
                BadRequestException.class,
                () -> userService.modifyUser(
                        1,
                        new ModifyUserRequest().enabled(false).email("email@test.test").roles(List.of(ROLE_USER.name(), ROLE_ADMIN.name()))));

        verify(userRepository, never()).save(any(User.class));
    }
}

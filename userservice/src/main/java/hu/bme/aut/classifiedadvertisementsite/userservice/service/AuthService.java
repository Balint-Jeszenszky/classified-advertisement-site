package hu.bme.aut.classifiedadvertisementsite.userservice.service;

import hu.bme.aut.classifiedadvertisementsite.userservice.api.external.model.LoginRequest;
import hu.bme.aut.classifiedadvertisementsite.userservice.api.external.model.RegistrationRequest;
import hu.bme.aut.classifiedadvertisementsite.userservice.api.external.model.ResetPasswordRequest;
import hu.bme.aut.classifiedadvertisementsite.userservice.api.external.model.UserDetailsResponse;
import hu.bme.aut.classifiedadvertisementsite.userservice.controller.exceptions.*;
import hu.bme.aut.classifiedadvertisementsite.userservice.mapper.UserMapper;
import hu.bme.aut.classifiedadvertisementsite.userservice.model.*;
import hu.bme.aut.classifiedadvertisementsite.userservice.repository.PasswordResetRepository;
import hu.bme.aut.classifiedadvertisementsite.userservice.repository.RoleRepository;
import hu.bme.aut.classifiedadvertisementsite.userservice.repository.UserRepository;
import hu.bme.aut.classifiedadvertisementsite.userservice.security.UserDetailsImpl;
import hu.bme.aut.classifiedadvertisementsite.userservice.service.util.EmailValidator;
import hu.bme.aut.classifiedadvertisementsite.userservice.service.util.PasswordValidator;
import hu.bme.aut.classifiedadvertisementsite.userservice.service.util.RandomStringGenerator;
import hu.bme.aut.classifiedadvertisementsite.userservice.service.util.UserValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailVerificationService emailVerificationService;
    private final PasswordResetRepository passwordResetRepository;
    private final NotificationService notificationService;

    @Transactional
    public void registerUser(RegistrationRequest registrationRequest) {
        String email = registrationRequest.getEmail().toLowerCase();
        String username = registrationRequest.getUsername().toLowerCase();

        if (!UserValidator.validateUsername(username)) {
            throw new BadRequestException("Error: Username should be at least 4 characters long!");
        }

        if (!EmailValidator.validateEmail(email)) {
            throw new BadRequestException("Error: Email is invalid!");
        }

        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Error: Email is already in use!");
        }

        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("Error: Username is already in use!");
        }

        if (!PasswordValidator.validatePassoword(registrationRequest.getPassword())) {
            throw new BadRequestException("Error: Password should be at least 8 character!");
        }

        if (!registrationRequest.getPassword().equals(registrationRequest.getConfirmPassword())) {
            throw new BadRequestException("Error: Passwords not match!");
        }

        Set<Role> roles = new HashSet<>();

        Optional<Role> role = roleRepository.findByName(ERole.ROLE_USER);

        if (role.isEmpty()) {
            log.info("No roles, creating them now");
            roleRepository.saveAllAndFlush(List.of(
                    Role.builder().name(ERole.ROLE_ADMIN).build(),
                    Role.builder().name(ERole.ROLE_USER).build()));
            roles.add(roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> {
                        log.error("Role {} not found duing registration", ERole.ROLE_ADMIN);
                        return new InternalServerErrorException("Error: Role is not found.");
                    }));
            log.info("Role {} added to new user {}", ERole.ROLE_ADMIN, username);
        }

        roles.add(roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> {
                    log.error("Role {} not found during registration", ERole.ROLE_USER);
                    return new InternalServerErrorException("Error: Role is not found.");
                }));

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .roles(roles)
                .build();

        userRepository.save(user);

        log.info("User {} registered successfully, id({})", username, user.getId());

        emailVerificationService.sendVerificationEmail(user, email);
    }

    public UserDetailsResponse login(LoginRequest loginRequest) {
        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername().toLowerCase().trim(),
                            loginRequest.getPassword()));
        } catch (AuthenticationException e) {
            log.error("Login failed with username {}", loginRequest.getUsername());
            throw new UnauthorizedException("Wrong credentials");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        log.info("User {} id({}) logged in successfully", userDetails.getUsername(), userDetails.getId());

        return UserMapper.INSTANCE.userDetailsToUserDetailsResponse(userDetails);
    }

    @Transactional
    public void sendResetMail(String email) {
        User user = userRepository.findByEmail(email.toLowerCase()).orElseThrow(() -> new NotFoundException("User not found"));

        passwordResetRepository.deleteAllByUser(user);
        passwordResetRepository.flush();

        String key = RandomStringGenerator.generate(32);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 30);

        PasswordReset passwordReset = PasswordReset.builder()
                .user(user)
                .key(key)
                .expiration(calendar.getTime())
                .build();

        passwordResetRepository.save(passwordReset);

        log.info("Sending password reset email to {}, key: {}", email, key);

        notificationService.sendEmail(email, "Reset password", key);
    }

    public void resetPassword(ResetPasswordRequest passwordResetDto) {
        PasswordReset passwordReset = passwordResetRepository.findByKey(passwordResetDto.getKey())
                .orElseThrow(() -> new ForbiddenException("Invalid key"));

        if (passwordReset.getExpiration().before(new Date())) {
            passwordResetRepository.delete(passwordReset);
            throw new BadRequestException("Link expired");
        }

        if (!passwordResetDto.getPassword().equals(passwordResetDto.getConfirmPassword())) {
            throw new BadRequestException("Two password not match");
        }

        if (passwordResetDto.getPassword().length() < 8) {
            throw new BadRequestException("Password should be at least 8 character");
        }

        User userToUpdate = passwordReset.getUser();

        userToUpdate.setPassword(passwordEncoder.encode(passwordResetDto.getPassword()));

        log.info("Successful password reset by user id({})", userToUpdate.getId());

        passwordResetRepository.delete(passwordReset);
    }

    @Scheduled(cron = "0 0 3 * * *", zone = "Europe/Budapest")
    void deleteExpiredPasswordResetKeys() {
        log.info("Deleting expired password reset keys");
        List<PasswordReset> expired = passwordResetRepository.findByExpirationLessThan(new Date());
        passwordResetRepository.deleteAllInBatch(expired);
        log.info("Deleted {} password reset keys", expired.size());
    }
}

package hu.bme.aut.classifiedadvertisementsite.userservice.service;

import hu.bme.aut.classifiedadvertisementsite.userservice.controller.exceptions.UnauthorizedException;
import hu.bme.aut.classifiedadvertisementsite.userservice.mapper.UserMapper;
import hu.bme.aut.classifiedadvertisementsite.userservice.model.*;
import hu.bme.aut.classifiedadvertisementsite.userservice.repository.RoleRepository;
import hu.bme.aut.classifiedadvertisementsite.userservice.repository.UserRepository;
import hu.bme.aut.classifiedadvertisementsite.userservice.controller.exceptions.BadRequestException;
import hu.bme.aut.classifiedadvertisementsite.userservice.controller.exceptions.InternalServerErrorException;
import hu.bme.aut.classifiedadvertisementsite.userservice.security.UserDetailsImpl;
import hu.bme.aut.classifiedadvertisementsite.userservice.service.util.EmailValidator;
import hu.bme.aut.classifiedadvertisementsite.userservice.service.util.PasswordValidator;
import hu.bme.aut.classifiedadvertisementsite.userservice.service.util.UserValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailVerificationService emailVerificationService;

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

        log.info("User {} registered successfully", username);

        userRepository.save(user);

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

        log.info("User {} logged in successfully", userDetails.getUsername());

        return UserMapper.INSTANCE.userDetailsToUserDetailsResponse(userDetails);
    }
}

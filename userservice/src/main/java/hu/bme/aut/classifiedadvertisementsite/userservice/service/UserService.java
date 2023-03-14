package hu.bme.aut.classifiedadvertisementsite.userservice.service;

import hu.bme.aut.classifiedadvertisementsite.userservice.controller.exceptions.BadRequestException;
import hu.bme.aut.classifiedadvertisementsite.userservice.controller.exceptions.NotFoundException;
import hu.bme.aut.classifiedadvertisementsite.userservice.mapper.UserMapper;
import hu.bme.aut.classifiedadvertisementsite.userservice.model.*;
import hu.bme.aut.classifiedadvertisementsite.userservice.repository.RoleRepository;
import hu.bme.aut.classifiedadvertisementsite.userservice.repository.UserRepository;
import hu.bme.aut.classifiedadvertisementsite.userservice.security.LoggedInUserService;
import hu.bme.aut.classifiedadvertisementsite.userservice.service.util.EmailValidator;
import hu.bme.aut.classifiedadvertisementsite.userservice.service.util.PasswordValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final LoggedInUserService loggedInUserService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final EmailVerificationService emailVerificationService;

    public UserDetailsResponse getLoggedInUserProfile() {
        Integer id = loggedInUserService.getLoggedInUser().getId();

        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));

        return UserMapper.INSTANCE.userToUserDetailsResponse(user);
    }

    public void modifyProfile(ModifyProfileRequest modifyProfileRequest) {
        Integer id = loggedInUserService.getLoggedInUser().getId();
        String email = modifyProfileRequest.getEmail();

        if (!EmailValidator.validateEmail(email)) {
            throw new BadRequestException("Error: Email is invalid!");
        }

        if (userRepository.existsByEmailAndIdNot(email, id)) {
            throw new BadRequestException("Error: Email is already in use!");
        }

        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));

        if (!user.getEmail().equals(email)) {
            emailVerificationService.sendVerificationEmail(user, email);
        }

        String oldPassword = modifyProfileRequest.getOldPassword();
        if (oldPassword != null && oldPassword.length() > 0) {
            if (!passwordEncoder.matches(modifyProfileRequest.getOldPassword(), user.getPassword())) {
                throw new BadRequestException("Wrong password");
            }

            if (!PasswordValidator.validatePassoword(modifyProfileRequest.getNewPassword())) {
                throw new BadRequestException("Error: Password should be at least 8 character!");
            }

            if (!modifyProfileRequest.getNewPassword().equals(modifyProfileRequest.getConfirmNewPassword())) {
                throw new BadRequestException("Error: Passwords not match!");
            }

            user.setPassword(passwordEncoder.encode(modifyProfileRequest.getNewPassword()));
        }

        userRepository.save(user);
    }

    public List<UserDetailsResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(UserMapper.INSTANCE::userToUserDetailsResponse).toList();
    }

    public UserDetailsResponse getUserById(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        return UserMapper.INSTANCE.userToUserDetailsResponse(user);
    }

    public void modifyUser(Integer userId, ModifyUserRequest modifyUserRequest) {
        User loggedInUser = loggedInUserService.getLoggedInUser();
        String email = modifyUserRequest.getEmail();

        if (!EmailValidator.validateEmail(email)) {
            throw new BadRequestException("Error: Email is invalid!");
        }

        if (userRepository.existsByEmailAndIdNot(email, userId)) {
            throw new BadRequestException("Error: Email is already in use!");
        }

        List<ERole> parsedRoles = modifyUserRequest.getRoles().stream().map(ERole::valueOf).toList();

        if (loggedInUser.getId().equals(userId) && !parsedRoles.contains(ERole.ROLE_ADMIN)) {
            throw new BadRequestException("Admins can not remove their own admin role");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        if (!user.getEmail().equals(email)) {
            emailVerificationService.sendVerificationEmail(user, email);
        }

        Set<Role> roles = roleRepository.findAllByNameIn(parsedRoles);

        user.setRoles(roles);

        userRepository.save(user);

        log.info("Admin {} successfully modified {}", loggedInUser.getUsername(), user.getUsername());
    }
}

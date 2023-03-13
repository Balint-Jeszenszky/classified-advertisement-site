package hu.bme.aut.classifiedadvertisementsite.userservice.controller;

import hu.bme.aut.classifiedadvertisementsite.userservice.api.UsersApi;
import hu.bme.aut.classifiedadvertisementsite.userservice.model.ModifyUserRequest;
import hu.bme.aut.classifiedadvertisementsite.userservice.model.UserDetailsResponse;
import hu.bme.aut.classifiedadvertisementsite.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UsersController implements UsersApi {
    private final UserService userService;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDetailsResponse>> getUsersAll() {
        List<UserDetailsResponse> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDetailsResponse> getUsersUserId(Integer userId) {
        UserDetailsResponse user = userService.getUserById(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> putUsersUserId(Integer userId, ModifyUserRequest modifyUserRequest) {
        userService.modifyUser(userId, modifyUserRequest);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}

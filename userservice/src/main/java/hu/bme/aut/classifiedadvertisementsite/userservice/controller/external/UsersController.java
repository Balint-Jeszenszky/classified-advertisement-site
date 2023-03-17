package hu.bme.aut.classifiedadvertisementsite.userservice.controller.external;

import hu.bme.aut.classifiedadvertisementsite.userservice.api.external.UsersApi;
import hu.bme.aut.classifiedadvertisementsite.userservice.api.external.model.ModifyUserRequest;
import hu.bme.aut.classifiedadvertisementsite.userservice.api.external.model.UserDetailsResponse;
import hu.bme.aut.classifiedadvertisementsite.userservice.mapper.UserMapper;
import hu.bme.aut.classifiedadvertisementsite.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UsersController implements UsersApi, ExternalApi {
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
        UserDetailsResponse userDetailsResponse = UserMapper.INSTANCE.userDataToUserDetailsResponse(userService.getUserById(userId));
        return new ResponseEntity<>(userDetailsResponse, HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> putUsersUserId(Integer userId, ModifyUserRequest modifyUserRequest) {
        userService.modifyUser(userId, modifyUserRequest);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}

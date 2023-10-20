package hu.bme.aut.classifiedadvertisementsite.userservice.controller.internal;

import hu.bme.aut.classifiedadvertisementsite.userservice.api.internal.UsersApi;
import hu.bme.aut.classifiedadvertisementsite.userservice.api.internal.model.UserDataResponse;
import hu.bme.aut.classifiedadvertisementsite.userservice.mapper.UserMapper;
import hu.bme.aut.classifiedadvertisementsite.userservice.service.UserService;
import hu.bme.aut.classifiedadvertisementsite.userservice.service.dto.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController implements UsersApi, InternalApi {
    private final UserService userService;

    @Override
    public ResponseEntity<List<UserDataResponse>> getUserDetailsIds(List<Integer> ids) {
        List<UserData> users = userService.getUserDataByIds(ids);
        List<UserDataResponse> userData = users.stream().map(UserMapper.INSTANCE::userDataToUserDataResponse).toList();
        return new ResponseEntity<>(userData, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserDataResponse> getUsersUserId(Integer userId) {
        UserDataResponse userDataResponse = UserMapper.INSTANCE.userDataToUserDataResponse(userService.getUserById(userId));
        return new ResponseEntity<>(userDataResponse, HttpStatus.OK);
    }
}

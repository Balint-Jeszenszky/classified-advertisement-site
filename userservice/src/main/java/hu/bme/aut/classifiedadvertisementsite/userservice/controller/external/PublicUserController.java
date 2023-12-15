package hu.bme.aut.classifiedadvertisementsite.userservice.controller.external;

import hu.bme.aut.classifiedadvertisementsite.userservice.api.external.PublicUserApi;
import hu.bme.aut.classifiedadvertisementsite.userservice.api.external.model.PublicUserDetailsResponse;
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
public class PublicUserController implements PublicUserApi, ExternalApi {
    private final UserService userService;

    @Override
    public ResponseEntity<List<PublicUserDetailsResponse>> getUserId(List<Integer> ids) {
        List<UserData> users = userService.getUserDataByIds(ids);
        List<PublicUserDetailsResponse> userDetails = users.stream().map(UserMapper.INSTANCE::userDataToPublicUserDetailsResponse).toList();
        return new ResponseEntity<>(userDetails, HttpStatus.OK);
    }
}

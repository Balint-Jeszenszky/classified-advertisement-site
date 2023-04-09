package hu.bme.aut.classifiedadvertisementsite.userservice.controller.external;

import hu.bme.aut.classifiedadvertisementsite.userservice.api.external.PublicUserApi;
import hu.bme.aut.classifiedadvertisementsite.userservice.api.external.model.PublicUserDetailsResponse;
import hu.bme.aut.classifiedadvertisementsite.userservice.service.UserService;
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
        List<PublicUserDetailsResponse> userDetails = userService.getPublicUserDetailsById(ids);
        return new ResponseEntity<>(userDetails, HttpStatus.OK);
    }
}

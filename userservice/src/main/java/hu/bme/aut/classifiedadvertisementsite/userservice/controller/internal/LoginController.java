package hu.bme.aut.classifiedadvertisementsite.userservice.controller.internal;

import hu.bme.aut.classifiedadvertisementsite.userservice.api.internal.LoginApi;
import hu.bme.aut.classifiedadvertisementsite.userservice.api.internal.model.LoginRequest;
import hu.bme.aut.classifiedadvertisementsite.userservice.api.internal.model.UserDataResponse;
import hu.bme.aut.classifiedadvertisementsite.userservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController implements LoginApi, InternalApi {
    private final AuthService authService;

    @Override
    public ResponseEntity<UserDataResponse> postAuthLogin(LoginRequest loginRequest) {
        UserDataResponse user = authService.login(loginRequest);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}

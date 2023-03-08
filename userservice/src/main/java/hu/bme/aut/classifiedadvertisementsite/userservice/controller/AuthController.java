package hu.bme.aut.classifiedadvertisementsite.userservice.controller;

import hu.bme.aut.classifiedadvertisementsite.userservice.api.LoginApi;
import hu.bme.aut.classifiedadvertisementsite.userservice.model.LoginRequest;
import hu.bme.aut.classifiedadvertisementsite.userservice.model.UserDetailsResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AuthController implements LoginApi {

    @Override
    public ResponseEntity<UserDetailsResponse> postLogin(LoginRequest loginRequest) {
        return new ResponseEntity<>(
                new UserDetailsResponse()
                        .id(5)
                        .username(loginRequest.getUsername())
                        .email("test@example.com")
                        .roles(List.of("ROLE_USER", "ROLE_ADMIN")),
                HttpStatus.OK);
    }
}

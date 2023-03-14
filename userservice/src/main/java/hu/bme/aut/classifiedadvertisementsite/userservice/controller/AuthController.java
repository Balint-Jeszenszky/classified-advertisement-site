package hu.bme.aut.classifiedadvertisementsite.userservice.controller;

import hu.bme.aut.classifiedadvertisementsite.userservice.api.AuthApi;
import hu.bme.aut.classifiedadvertisementsite.userservice.model.*;
import hu.bme.aut.classifiedadvertisementsite.userservice.service.AuthService;
import hu.bme.aut.classifiedadvertisementsite.userservice.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;

    @Override
    public ResponseEntity<Void> postAuthReguster(RegistrationRequest registrationRequest) {
        authService.registerUser(registrationRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> postAuthResetPassword(String code, ResetPasswordRequest resetPasswordRequest) {
        return null; // TODO
    }

    @Override
    public ResponseEntity<Void> postAuthVerifyEmail(VerifyEmailRequest verifyEmailRequest) {
        emailVerificationService.validateKey(verifyEmailRequest.getKey());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<UserDetailsResponse> postAuthLogin(LoginRequest loginRequest) {
        UserDetailsResponse user = authService.login(loginRequest);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}

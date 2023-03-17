package hu.bme.aut.classifiedadvertisementsite.userservice.controller.external;

import hu.bme.aut.classifiedadvertisementsite.userservice.api.external.AuthApi;
import hu.bme.aut.classifiedadvertisementsite.userservice.api.external.model.*;
import hu.bme.aut.classifiedadvertisementsite.userservice.service.AuthService;
import hu.bme.aut.classifiedadvertisementsite.userservice.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi, ExternalApi {

    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;

    @Override
    public ResponseEntity<Void> postAuthRegister(RegistrationRequest registrationRequest) {
        authService.registerUser(registrationRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> postAuthResetPassword(ForgotPasswordRequest forgotPasswordRequest) {
        authService.sendResetMail(forgotPasswordRequest.getEmail());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<Void> postAuthVerifyEmail(VerifyEmailRequest verifyEmailRequest) {
        emailVerificationService.validateKey(verifyEmailRequest.getKey());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<Void> putAuthResetPassword(ResetPasswordRequest resetPasswordRequest) {
        authService.resetPassword(resetPasswordRequest);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<UserDetailsResponse> postAuthLogin(LoginRequest loginRequest) {
        UserDetailsResponse user = authService.login(loginRequest);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}

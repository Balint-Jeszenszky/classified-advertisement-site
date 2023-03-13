package hu.bme.aut.classifiedadvertisementsite.userservice.controller;

import hu.bme.aut.classifiedadvertisementsite.userservice.api.ProfileApi;
import hu.bme.aut.classifiedadvertisementsite.userservice.model.ModifyProfileRequest;
import hu.bme.aut.classifiedadvertisementsite.userservice.model.UserDetailsResponse;
import hu.bme.aut.classifiedadvertisementsite.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserProfileController implements ProfileApi {
    private final UserService userService;

    @Override
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDetailsResponse> getProfile() {
        return new ResponseEntity<>(userService.getLoggedInUserProfile(), HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> putProfile(ModifyProfileRequest modifyProfileRequest) {
        userService.modifyProfile(modifyProfileRequest);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}

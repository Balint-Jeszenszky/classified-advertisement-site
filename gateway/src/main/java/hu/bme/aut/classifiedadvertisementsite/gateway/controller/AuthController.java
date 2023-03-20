package hu.bme.aut.classifiedadvertisementsite.gateway.controller;

import hu.bme.aut.classifiedadvertisementsite.gateway.api.AuthApi;
import hu.bme.aut.classifiedadvertisementsite.gateway.api.model.*;
import hu.bme.aut.classifiedadvertisementsite.gateway.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {
    private final AuthService authService;

    @Override
    public Mono<ResponseEntity<Void>> deleteApiAuthLogout(Mono<LogoutRequest> logoutRequest, ServerWebExchange exchange) {
        return logoutRequest.flatMap(r -> {
            authService.logout(r.getRefreshToken());
            return Mono.just(new ResponseEntity<>(HttpStatus.NO_CONTENT));
        });
    }

    @Override
    public Mono<ResponseEntity<RefreshResponse>> postApiAuthRefresh(Mono<RefreshRequest> refreshRequest, ServerWebExchange exchange) {
        return refreshRequest.flatMap(r -> {
            RefreshResponse refreshResponse = authService.refreshLogin(r.getRefreshToken());
            return Mono.just(new ResponseEntity<>(refreshResponse, HttpStatus.CREATED));
        });
    }

    @Override
    public Mono<ResponseEntity<LoginResponse>> postAuthLogin(Mono<LoginRequest> loginRequest, ServerWebExchange exchange) {
        return loginRequest.flatMap(r -> {
            LoginResponse loginResponse = authService.login(r.getUsername(), r.getPassword());
            return Mono.just(new ResponseEntity<>(loginResponse, HttpStatus.OK));
        });
    }
}

package hu.bme.aut.classifiedadvertisementsite.gateway.controller;

import hu.bme.aut.classifiedadvertisementsite.gateway.api.AuthApi;
import hu.bme.aut.classifiedadvertisementsite.gateway.api.model.LogoutRequest;
import hu.bme.aut.classifiedadvertisementsite.gateway.api.model.RefreshRequest;
import hu.bme.aut.classifiedadvertisementsite.gateway.api.model.RefreshResponse;
import hu.bme.aut.classifiedadvertisementsite.gateway.service.AuthService;
import lombok.RequiredArgsConstructor;
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
        return null;
    }

    @Override
    public Mono<ResponseEntity<RefreshResponse>> postApiAuthRefresh(Mono<RefreshRequest> refreshRequest, ServerWebExchange exchange) {
        return null;
    }


}

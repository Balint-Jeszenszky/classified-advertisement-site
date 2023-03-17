package hu.bme.aut.classifiedadvertisementsite.gateway.service;

import hu.bme.aut.classifiedadvertisementsite.gateway.repository.RefreshTokenRepository;
import hu.bme.aut.classifiedadvertisementsite.gateway.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;

    public void logout(String refreshToken) {
    }
}

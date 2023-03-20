package hu.bme.aut.classifiedadvertisementsite.gateway.service;

import hu.bme.aut.classifiedadvertisementsite.gateway.api.model.LoginResponse;
import hu.bme.aut.classifiedadvertisementsite.gateway.api.model.RefreshResponse;
import hu.bme.aut.classifiedadvertisementsite.gateway.api.model.UserDetailsResponse;
import hu.bme.aut.classifiedadvertisementsite.gateway.client.userservice.api.AuthenticationApi;
import hu.bme.aut.classifiedadvertisementsite.gateway.client.userservice.api.UsersApi;
import hu.bme.aut.classifiedadvertisementsite.gateway.client.userservice.api.model.LoginRequest;
import hu.bme.aut.classifiedadvertisementsite.gateway.client.userservice.api.model.UserDataResponse;
import hu.bme.aut.classifiedadvertisementsite.gateway.controller.exception.InternalServerErrorException;
import hu.bme.aut.classifiedadvertisementsite.gateway.controller.exception.UnauthorizedException;
import hu.bme.aut.classifiedadvertisementsite.gateway.model.RefreshToken;
import hu.bme.aut.classifiedadvertisementsite.gateway.repository.RefreshTokenRepository;
import hu.bme.aut.classifiedadvertisementsite.gateway.security.jwt.JwtUtils;
import hu.bme.aut.classifiedadvertisementsite.gateway.security.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
public class AuthService {
    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UsersApi usersApi;
    private final AuthenticationApi authenticationApi;

    @Transactional
    public void logout(String refreshToken) {
        Integer userId = jwtUtils.getUserIdFromRefreshToken(refreshToken);
        log.info("User id({}) logged out", userId);
        refreshTokenRepository.deleteByUserIdAndTokenHash(userId, getHashOfJwtToken(refreshToken));
    }

    @Transactional
    public RefreshResponse refreshLogin(String refreshToken) {
        if (!jwtUtils.validateJwtRefreshToken(refreshToken)) {
            throw new UnauthorizedException("Wrong refresh token");
        }

        String hashOfJwtToken = getHashOfJwtToken(refreshToken);
        Integer userId = jwtUtils.getUserIdFromRefreshToken(refreshToken);
        Optional<RefreshToken> storedToken = refreshTokenRepository.findFirstByTokenHashAndUserId(hashOfJwtToken, userId);

        if (storedToken.isEmpty()) {
            log.error("Token not found for user id({}), hash: {}", userId, hashOfJwtToken);
            throw new UnauthorizedException("Wrong refresh token");
        }

        refreshTokenRepository.delete(storedToken.get());

        UserDataResponse userData = usersApi.getUsersUserId(userId);

        User user = userDataResponseToUser(userData);

        String jwt = jwtUtils.generateJwtToken(user);
        String refreshJwt = jwtUtils.generateJwtRefreshToken(user);

        RefreshResponse newTokenResponse = new RefreshResponse()
                .accessToken(jwt)
                .refreshToken(refreshJwt);

        saveRefreshToken(refreshJwt, user);

        log.info("User id({}) refreshed login", userId);

        return newTokenResponse;
    }

    public LoginResponse login(String username, String password) {
        UserDataResponse userData = authenticationApi.postAuthLogin(new LoginRequest().username(username).password(password));
        User user = userDataResponseToUser(userData);

        String jwt = jwtUtils.generateJwtToken(user);
        String refreshJwt = jwtUtils.generateJwtRefreshToken(user);

        saveRefreshToken(refreshJwt, user);

        log.info("User id({}) logged in", user.getId());

        return new LoginResponse()
                .user(new UserDetailsResponse()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .roles(user.getRoles()))
                .accessToken(jwt)
                .refreshToken(refreshJwt);
    }

    private User userDataResponseToUser(UserDataResponse userData) {
        return new User(
                userData.getId(),
                userData.getUsername(),
                userData.getEmail(),
                userData.getRoles());
    }

    private void saveRefreshToken(String token, User user) {
        String tokenHash = getHashOfJwtToken(token);
        Date expiration = jwtUtils.getRefreshTokenExpiration(token);
        refreshTokenRepository.save(RefreshToken.builder()
                .tokenHash(tokenHash)
                .expiration(expiration)
                .userId(user.getId())
                .build());
    }

    private String getHashOfJwtToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return String.copyValueOf(Hex.encode(hash));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 3 * * *", zone = "Europe/Budapest")
    protected void clearExpiredRefreshTokens() {
        log.info("Deleting expired refresh tokens");
        List<RefreshToken> expired = refreshTokenRepository.findByExpirationLessThan(new Date());
        refreshTokenRepository.deleteAllInBatch(expired);
        log.info("Deleted {} refresh tokens", expired.size());
    }
}

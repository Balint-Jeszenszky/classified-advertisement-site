package hu.bme.aut.classifiedadvertisementsite.gateway.security.jwt;

import hu.bme.aut.classifiedadvertisementsite.gateway.security.model.User;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class JwtUtils {

    @Value("${gateway.auth.jwtSecret}")
    private String jwtSecret;

    @Value("${gateway.auth.jwtRefreshSecret}")
    private String jwtRefreshSecret;

    @Value("${gateway.auth.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${gateway.auth.jwtRefreshExpirationMs}")
    private int jwtRefreshExpirationMs;

    private static final String ROLES = "roles";
    private static final String EMAIL = "email";
    private static final String ID = "id";

    public String generateJwtToken(User user) {

        return Jwts.builder()
                .setSubject((user.getUsername()))
                .setIssuedAt(new Date())
                .claim(ID, user.getId())
                .claim(EMAIL, user.getEmail())
                .claim(ROLES, user.getRoles())
                .claim("type", "access")
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String generateJwtRefreshToken(User user) {

        return Jwts.builder()
                .setSubject((user.getUsername()))
                .claim(ID, user.getId())
                .setIssuedAt(new Date())
                .claim("type", "refresh")
                .setExpiration(new Date((new Date()).getTime() + jwtRefreshExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtRefreshSecret)
                .compact();
    }

    public boolean validateJwtToken(String authToken) {
        return validateToken(authToken, jwtSecret);
    }

    public boolean validateJwtRefreshToken(String refreshToken) {
        return validateToken(refreshToken, jwtRefreshSecret);
    }

    public User getUserFromJwt(String accessToken) {
        if (!validateJwtToken(accessToken)) {
            return null;
        }

        Integer id = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(accessToken).getBody().get(ID, Integer.class);
        String username = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(accessToken).getBody().getSubject();
        String email = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(accessToken).getBody().get(EMAIL, String.class);
        List<String> roles = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(accessToken).getBody().get(ROLES, List.class);

        return new User(id, username, email, roles);
    }

    public Integer getUserIdFromRefreshToken(String refreshToken) {
        if (!validateJwtRefreshToken(refreshToken)) {
            return null;
        }

        return Jwts.parser().setSigningKey(jwtRefreshSecret).parseClaimsJws(refreshToken).getBody().get(ID, Integer.class);
    }

    public Date getRefreshTokenExpiration(String token) {
        return Jwts.parser().setSigningKey(jwtRefreshSecret).parseClaimsJws(token).getBody().getExpiration();
    }

    private boolean validateToken(String token, String secret) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
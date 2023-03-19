package hu.bme.aut.classifiedadvertisementsite.gateway.repository;

import hu.bme.aut.classifiedadvertisementsite.gateway.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    @Modifying
    void deleteByUserIdAndTokenHash(Integer userId, String tokenHash);

    Optional<RefreshToken> findFirstByTokenHashAndUserId(String tokenHash, Integer userId);

    List<RefreshToken> findByExpirationLessThan(Date expiration);
}

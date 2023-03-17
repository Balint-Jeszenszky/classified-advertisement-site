package hu.bme.aut.classifiedadvertisementsite.gateway.repository;

import hu.bme.aut.classifiedadvertisementsite.gateway.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    @Modifying
    void deleteByUserIdAndTokenHash(Integer userId, String tokenHash);
}

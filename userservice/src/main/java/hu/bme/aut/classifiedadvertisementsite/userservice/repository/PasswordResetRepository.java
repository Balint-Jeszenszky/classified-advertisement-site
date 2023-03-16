package hu.bme.aut.classifiedadvertisementsite.userservice.repository;

import hu.bme.aut.classifiedadvertisementsite.userservice.model.PasswordReset;
import hu.bme.aut.classifiedadvertisementsite.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, Integer> {
    @Modifying
    void deleteAllByUser(User user);

    Optional<PasswordReset> findByKey(String key);

    List<PasswordReset> findByExpirationLessThan(Date date);
}

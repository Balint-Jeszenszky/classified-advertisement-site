package hu.bme.aut.classifiedadvertisementsite.userservice.repository;

import hu.bme.aut.classifiedadvertisementsite.userservice.model.EmailVerification;
import hu.bme.aut.classifiedadvertisementsite.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Integer> {
    Optional<EmailVerification> getEmailVerificationByKey(String key);
    @Modifying
    void deleteAllByUser(User user);
}
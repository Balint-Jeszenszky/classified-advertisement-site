package hu.bme.aut.classifiedadvertisementsite.userservice.repository;

import hu.bme.aut.classifiedadvertisementsite.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Boolean existsByEmail(String email);

    Boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);
}
package hu.bme.aut.classifiedadvertisementsite.userservice.repository;

import hu.bme.aut.classifiedadvertisementsite.userservice.model.ERole;
import hu.bme.aut.classifiedadvertisementsite.userservice.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(ERole name);
    Set<Role> findAllByNameIn(List<ERole> names);
}
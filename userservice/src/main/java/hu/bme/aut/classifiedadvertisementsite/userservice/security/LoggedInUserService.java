package hu.bme.aut.classifiedadvertisementsite.userservice.security;

import hu.bme.aut.classifiedadvertisementsite.userservice.model.ERole;
import hu.bme.aut.classifiedadvertisementsite.userservice.model.Role;
import hu.bme.aut.classifiedadvertisementsite.userservice.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static hu.bme.aut.classifiedadvertisementsite.userservice.model.ERole.ROLE_ADMIN;

@Service
public class LoggedInUserService {

    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Set<Role> roles = userDetails.getAuthorities()
                .stream()
                .map(a -> Role.builder().name(ERole.valueOf(a.getAuthority())).build())
                .collect(Collectors.toSet());

        return  User.builder()
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .roles(roles)
                .build();
    }

    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream().map(GrantedAuthority::getAuthority).toList().contains(ROLE_ADMIN.name());
    }
}
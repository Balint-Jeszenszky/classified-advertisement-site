package hu.bme.aut.classifiedadvertisementsite.userservice.security;

import hu.bme.aut.classifiedadvertisementsite.userservice.controller.exceptions.NotFoundException;
import hu.bme.aut.classifiedadvertisementsite.userservice.model.User;
import hu.bme.aut.classifiedadvertisementsite.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            log.error("User not found by username: {}", username);
            throw new NotFoundException("User not found");
        }

        return UserDetailsImpl.build(user.get());
    }
}
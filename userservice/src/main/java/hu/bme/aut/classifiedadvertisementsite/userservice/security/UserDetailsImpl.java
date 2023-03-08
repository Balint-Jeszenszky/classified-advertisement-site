package hu.bme.aut.classifiedadvertisementsite.userservice.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {

    private Integer id;
    private String username;

    private String email;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Integer id, String username, String email, List<String> roles) {
        List<GrantedAuthority> authorities =
                roles.stream().map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toList());

        this.id = id;
        this.username = username;
        this.email = email;
        this.authorities = authorities;
    }

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}

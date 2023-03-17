package hu.bme.aut.classifiedadvertisementsite.userservice.mapper;

import hu.bme.aut.classifiedadvertisementsite.userservice.api.internal.model.UserDataResponse;
import hu.bme.aut.classifiedadvertisementsite.userservice.model.ERole;
import hu.bme.aut.classifiedadvertisementsite.userservice.model.Role;
import hu.bme.aut.classifiedadvertisementsite.userservice.model.User;
import hu.bme.aut.classifiedadvertisementsite.userservice.api.external.model.UserDetailsResponse;
import hu.bme.aut.classifiedadvertisementsite.userservice.security.UserDetailsImpl;
import hu.bme.aut.classifiedadvertisementsite.userservice.service.dto.UserData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserData userToUserData(User user);

    UserDetailsResponse userToUserDetailsResponse(User user);

    UserDetailsResponse userDataToUserDetailsResponse(UserData user);

    UserDataResponse userDataToUserDataResponse(UserData user);

    @Mapping(source = "authorities", target = "roles")
    User userDetailsToUser(UserDetailsImpl userDetails);

    @Mapping(source = "authorities", target = "roles")
    UserDetailsResponse userDetailsToUserDetailsResponse(UserDetailsImpl userDetails);

    default List<String> mapRoles(Set<Role> roles) {
        return roles.stream().map(role -> role.getName().name()).toList();
    }

    default Set<Role> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream().map(e -> Role.builder()
                .name(ERole.valueOf(e.getAuthority()))
                .build()).collect(Collectors.toSet());
    }
}

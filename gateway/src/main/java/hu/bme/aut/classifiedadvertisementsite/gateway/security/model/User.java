package hu.bme.aut.classifiedadvertisementsite.gateway.security.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class User {
    private Integer id;
    private String username;
    private String email;
    private List<String> roles;
}
package hu.bme.aut.classifiedadvertisementsite.userservice.service.dto;

import java.util.List;

public record UserData(Integer id, String username, String email, List<String> roles, Boolean enabled) {
}

package hu.bme.aut.classifiedadvertisementsite.userservice.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
public class AuthHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("x-user-data");

        if (authHeader != null) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node;
            try {
                node = mapper.readTree(Base64.getDecoder().decode(authHeader));

                UserDetails userDetails = new UserDetailsImpl(node.get("id").asInt(), node.get("username").asText(), node.get("email").asText(), jsonArrayToList(node.get("roles")));

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                log.error("Cannot set user authentication: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    private List<String> jsonArrayToList(JsonNode node) {
        List<String> list = new ArrayList<>();
        if (node.isArray()) {
            for (final JsonNode objNode : node) {
                list.add(objNode.asText());
            }
        }
        return list;
    }
}
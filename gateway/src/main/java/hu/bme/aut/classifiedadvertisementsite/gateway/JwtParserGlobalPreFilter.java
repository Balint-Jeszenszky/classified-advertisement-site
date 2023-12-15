package hu.bme.aut.classifiedadvertisementsite.gateway;

import hu.bme.aut.classifiedadvertisementsite.gateway.security.jwt.JwtUtils;
import hu.bme.aut.classifiedadvertisementsite.gateway.security.model.User;
import hu.bme.aut.classifiedadvertisementsite.gateway.security.util.AuthHeaderGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Base64;

@Component
@RequiredArgsConstructor
public class JwtParserGlobalPreFilter implements GlobalFilter {

    private final JwtUtils jwtUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        exchange.getRequest().mutate().headers(httpHeaders -> httpHeaders.remove("x-user-data"));
        if (exchange.getRequest().getHeaders().containsKey("authorization")) {
            String token = exchange.getRequest().getHeaders().get("authorization").get(0).substring(7);

            if (jwtUtils.validateJwtToken(token)) {
                User user = jwtUtils.getUserFromJwt(token);

                String authHeader = AuthHeaderGenerator.createAuthHeader(user);

                exchange.getRequest()
                        .mutate()
                        .headers(httpHeaders -> httpHeaders.set("x-user-data", Base64.getEncoder().encodeToString(authHeader.getBytes())));
            }
        }
        return chain.filter(exchange);
    }
}

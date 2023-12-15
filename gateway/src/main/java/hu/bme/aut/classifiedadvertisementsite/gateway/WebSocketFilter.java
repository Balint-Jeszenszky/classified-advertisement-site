package hu.bme.aut.classifiedadvertisementsite.gateway;

import hu.bme.aut.classifiedadvertisementsite.gateway.security.jwt.JwtUtils;
import hu.bme.aut.classifiedadvertisementsite.gateway.security.model.User;
import hu.bme.aut.classifiedadvertisementsite.gateway.security.util.AuthHeaderGenerator;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.WebsocketRoutingFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.headers.HttpHeadersFilter;
import org.springframework.http.HttpCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.List;

@Component
public class WebSocketFilter extends WebsocketRoutingFilter {
    private static final String JWT_KEY = "jwt";

    private final JwtUtils jwtUtils;

    public WebSocketFilter(WebSocketClient webSocketClient, WebSocketService webSocketService, ObjectProvider<List<HttpHeadersFilter>> headersFiltersProvider, JwtUtils jwtUtils) {
        super(webSocketClient, webSocketService, headersFiltersProvider);
        this.jwtUtils = jwtUtils;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        exchange.getRequest().mutate().headers(httpHeaders -> httpHeaders.remove("x-user-data"));
        exchange.getRequest().mutate().headers(httpHeaders -> httpHeaders.remove("origin"));

        if (exchange.getRequest().getPath().toString().startsWith("/api/bid")) {
            MultiValueMap<String, HttpCookie> cookies = exchange.getRequest().getCookies();

            if (cookies.containsKey(JWT_KEY) && jwtUtils.validateJwtToken(cookies.get(JWT_KEY).get(0).getValue())) {
                User user = jwtUtils.getUserFromJwt(cookies.get(JWT_KEY).get(0).getValue());

                String authHeader = AuthHeaderGenerator.createAuthHeader(user);

                exchange.getRequest()
                        .mutate()
                        .headers(httpHeaders -> httpHeaders.set("x-user-data", Base64.getEncoder().encodeToString(authHeader.getBytes())));
            }
        }

        return chain.filter(exchange);
    }
}

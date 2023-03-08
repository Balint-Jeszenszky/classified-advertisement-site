package hu.bme.aut.classifiedadvertisementsite.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import hu.bme.aut.classifiedadvertisementsite.gateway.security.jwt.JwtUtils;
import hu.bme.aut.classifiedadvertisementsite.gateway.security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Base64;

@Component
public class JwtParserGlobalPreFilter implements GlobalFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        exchange.getRequest().mutate().headers(httpHeaders -> httpHeaders.remove("x-user-data"));
        if (exchange.getRequest().getHeaders().containsKey("authorization")) {
            String token = exchange.getRequest().getHeaders().get("authorization").get(0).substring(7);

            if (token != null && jwtUtils.validateJwtToken(token)) {
                User user = jwtUtils.getUserFromJwt(token);

                ObjectMapper mapper = new ObjectMapper();
                ObjectNode node = mapper.createObjectNode();

                node.put("id", user.getId());
                node.put("username", user.getUsername());
                node.put("email", user.getEmail());
                ArrayNode arrayNode = mapper.createArrayNode();
                user.getRoles().forEach(arrayNode::add);
                node.set("roles", arrayNode);

                exchange.getRequest()
                        .mutate()
                        .headers(httpHeaders -> httpHeaders.set("x-user-data", Base64.getEncoder().encodeToString(node.toString().getBytes())));
            }
        }
        return chain.filter(exchange);
    }
}

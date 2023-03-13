package hu.bme.aut.classifiedadvertisementsite.gateway;

import hu.bme.aut.classifiedadvertisementsite.gateway.security.jwt.JwtUtils;
import hu.bme.aut.classifiedadvertisementsite.gateway.security.model.User;
import hu.bme.aut.classifiedadvertisementsite.gateway.security.model.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class GatewayApplication {

	@Autowired
	private JwtUtils jwtUtils;

	@Bean
	public RouteLocator myRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(p -> p
						.path("/api/user/auth/login")
						.filters(f -> f.modifyResponseBody(
								User.class,
								UserData.class,
								(exchange, s) -> Mono.just(
										new UserData(
												new User(s.getId(), s.getUsername(), s.getEmail(), s.getRoles()),
												jwtUtils.generateJwtToken(s),
												jwtUtils.generateJwtRefreshToken(s)))))
						.uri("http://localhost:8081"))
				.route(p -> p
						.path("/api/user/**")
						.uri("http://localhost:8081"))
				.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

}

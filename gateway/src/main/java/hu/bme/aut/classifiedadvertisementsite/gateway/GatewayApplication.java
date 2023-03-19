package hu.bme.aut.classifiedadvertisementsite.gateway;

import hu.bme.aut.classifiedadvertisementsite.gateway.security.model.User;
import hu.bme.aut.classifiedadvertisementsite.gateway.security.model.UserData;
import hu.bme.aut.classifiedadvertisementsite.gateway.service.AuthService;
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
	private AuthService authService;

	@Bean
	public RouteLocator myRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(p -> p
						.path("/api/user/auth/login")
						.filters(f -> f
								.rewritePath("/api/user/", "/api/user/external/")
								.modifyResponseBody(
										User.class,
										UserData.class,
										(exchange, user) -> Mono.just(authService.login(user))))
						.uri("http://localhost:8081"))
				.route(p -> p
						.path("/api/user/**")
						.filters(f -> f.rewritePath("/api/user/", "/api/user/external/"))
						.uri("http://localhost:8081"))
				.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

}

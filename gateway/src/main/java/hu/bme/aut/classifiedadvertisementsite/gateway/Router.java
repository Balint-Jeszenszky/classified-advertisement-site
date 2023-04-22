package hu.bme.aut.classifiedadvertisementsite.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Router {

    @Value("${gateway.services.userservice}")
    private String userServiceUri;
    @Value("${gateway.services.advertisementservice}")
    private String advertisementServiceUri;
    @Value("${gateway.services.imageService}")
    private String imageServiceUri;

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/api/user/**")
                        .filters(f -> f.rewritePath("/api/user/", "/api/user/external/"))
                        .uri(userServiceUri))
                .route(p -> p
                        .path("/api/advertisement/**")
                        .filters(f -> f.rewritePath("/api/advertisement/", "/api/advertisement/external/"))
                        .uri(advertisementServiceUri))
                .route(p -> p
                        .path("/api/images/**")
                        .filters(f -> f.rewritePath("/api/images/", "/api/images/external/"))
                        .uri(imageServiceUri))
                .build();
    }
}

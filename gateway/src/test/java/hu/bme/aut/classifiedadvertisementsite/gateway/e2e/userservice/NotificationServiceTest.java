package hu.bme.aut.classifiedadvertisementsite.gateway.e2e.userservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import hu.bme.aut.classifiedadvertisementsite.gateway.security.jwt.JwtUtils;
import hu.bme.aut.classifiedadvertisementsite.gateway.security.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NotificationServiceTest {
    private static final String API_PREFIX = "/api/notification";
    private static final String AUTHORIZATION_HEADER = "authorization";
    private static final String BEARER = "bearer ";

    private String userJwt;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JwtUtils jwtUtils;

    @BeforeAll
    void init() throws JsonProcessingException {
        userJwt = jwtUtils.generateJwtToken(new User(2, "user", "user@test.local", List.of("ROLE_USER")));
    }

    @Test
    void getPublicVapidKey() {
        webTestClient.get()
                .uri(API_PREFIX + "/publicVapidKey")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER, BEARER + userJwt)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void pushSubscription() {
        webTestClient.post()
                .uri(API_PREFIX + "/pushSubscription")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER, BEARER + userJwt)
                .body(BodyInserters.fromObject("{" +
                        "\"endpoint\":\"endpoint\"," +
                        "\"keys\":{" +
                        "\"p256dh\":\"p256dh\"," +
                        "\"auth\":\"auth\"" +
                        "}" +
                        "}"))
                .exchange()
                .expectStatus().isCreated();
    }
}

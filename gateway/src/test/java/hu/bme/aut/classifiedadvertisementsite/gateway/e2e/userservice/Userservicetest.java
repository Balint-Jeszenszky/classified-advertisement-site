package hu.bme.aut.classifiedadvertisementsite.gateway.e2e.userservice;

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
class Userservicetest {
    private static final String API_PREFIX = "/api/user";
    private static final String AUTHORIZATION_HEADER = "authorization";
    private static final String BEARER = "bearer ";

    private String adminJwt;
    private String userJwt;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JwtUtils jwtUtils;

    @BeforeAll
    void init() {
        adminJwt = jwtUtils.generateJwtToken(new User(1, "admin", "admin@test.local", List.of("ROLE_ADMIN", "ROLE_USER")));
        userJwt = jwtUtils.generateJwtToken(new User(2, "user", "user@test.local", List.of("ROLE_USER")));
    }

    @Test
    void registerUser() {
        webTestClient.post()
                .uri(API_PREFIX + "/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject("{" +
                        "\"username\": \"e2euser\"," +
                        "\"email\": \"e2euser@e2etest.local\"," +
                        "\"password\": \"Password\"," +
                        "\"confirmPassword\": \"Password\"" +
                        "}"))
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void requestResetPassword() throws Exception {
        webTestClient.post()
                .uri(API_PREFIX + "/auth/resetPassword")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject("{" +
                        "\"email\": \"user@test.local\"" +
                        "}"))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void resetPassword() {
        webTestClient.put()
                .uri(API_PREFIX + "/auth/resetPassword")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject("{" +
                        "\"key\": \"nonExistingKey\"," +
                        "\"password\": \"newPassword\"," +
                        "\"confirmPassword\": \"newPassword\"" +
                        "}"))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void verifyEmail() {
        webTestClient.post()
                .uri(API_PREFIX + "/auth/verifyEmail")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject("{" +
                        "\"key\": \"nonExistingKey\"" +
                        "}"))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getUserByIdForAdmin() {
        webTestClient.get()
                .uri(API_PREFIX + "/users/2")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER, BEARER + adminJwt)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void modifyUser() {
        webTestClient.put()
                .uri(API_PREFIX + "/users/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER, BEARER + adminJwt)
                .body(BodyInserters.fromObject("{" +
                        "\"email\": \"user@test.local\"," +
                        "\"roles\": [" +
                        "\"ROLE_USER\"" +
                        "]," +
                        "\"enabled\": true" +
                        "}"))
                .exchange()
                .expectStatus().isAccepted();
    }

    @Test
    void listAllUsers() {
        webTestClient.get()
                .uri(API_PREFIX + "/users/all")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER, BEARER + adminJwt)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getProfile() {
        webTestClient.get()
                .uri(API_PREFIX + "/profile")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER, BEARER + userJwt)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void modifyProfile() {
        webTestClient.put()
                .uri(API_PREFIX + "/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER, BEARER + userJwt)
                .body(BodyInserters.fromObject("{" +
                        "\"email\": \"user@test.local\"," +
                        "\"oldPassword\": \"UserPass\"," +
                        "\"newPassword\": \"UserPass\"," +
                        "\"confirmNewPassword\": \"UserPass\"" +
                        "}"))
                .exchange()
                .expectStatus().isAccepted();
    }

    @Test
    void getPublicUserById() {
        webTestClient.get()
                .uri(API_PREFIX + "/publicUser/2")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }
}

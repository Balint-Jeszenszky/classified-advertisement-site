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
public class ChatServiceTest {
    private static final String API_PREFIX = "/api/chat";
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
    void chatWebsocketConnection() {
        webTestClient.get()
                .uri(API_PREFIX + "/chat-ws/?transport=polling&EIO=4")
                .header(AUTHORIZATION_HEADER, BEARER + userJwt)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void listChatsForUser() {
        webTestClient.post()
                .uri(API_PREFIX + "/graphql")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER, BEARER + userJwt)
                .body(BodyInserters.fromObject("{" +
                        "  \"operationName\": \"GetChatsForUser\"," +
                        "  \"variables\": {}," +
                        "  \"query\": \"\\nquery GetChatsForUser {\\n  chatsForUser {\\n    id\\n    advertisementId\\n    advertisementOwnerUserId\\n    fromUserId\\n    messages {\\n      createdAt\\n      text\\n      userId\\n    }\\n  }\\n}\\n\"" +
                        "}"))
                .exchange()
                .expectStatus().isOk();
    }
}

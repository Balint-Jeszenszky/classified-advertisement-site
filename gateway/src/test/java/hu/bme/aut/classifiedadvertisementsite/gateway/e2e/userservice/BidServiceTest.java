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
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BidServiceTest {
    private static final String API_PREFIX = "/api/bid";

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    void getCurrentBidsForAds() {
        webTestClient.get()
                .uri(API_PREFIX + "/currentBids/1,2,3")
                .exchange()
                .expectStatus().isOk();
    }
}

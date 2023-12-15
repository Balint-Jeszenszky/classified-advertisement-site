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

import static hu.bme.aut.classifiedadvertisementsite.gateway.e2e.userservice.util.TestHelper.getJsonNode;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Webscraperservicetest {
    private static final String API_PREFIX = "/api/scraper";
    private static final String AUTHORIZATION_HEADER = "authorization";
    private static final String BEARER = "bearer ";
    private String editableSiteId;
    private String deletableSiteId;

    private String adminJwt;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JwtUtils jwtUtils;

    @BeforeAll
    void init() throws JsonProcessingException {
        adminJwt = jwtUtils.generateJwtToken(new User(1, "admin", "admin@test.local", List.of("ROLE_ADMIN", "ROLE_USER")));
        editableSiteId = createSiteAndReturnId();
        deletableSiteId = createSiteAndReturnId();
    }

    @Test
    void getProductByAdvertisementId() {
        webTestClient.get()
                .uri(API_PREFIX + "/advertisement/{id}", 1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getSites() {
        webTestClient.get()
                .uri(API_PREFIX + "/sites")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER, BEARER + adminJwt)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void createSite() {
        webTestClient.post()
                .uri(API_PREFIX + "/site")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER, BEARER + adminJwt)
                .body(BodyInserters.fromObject("{" +
                        "  \"categoryIds\": [1]," +
                        "  \"name\": \"string\"," +
                        "  \"url\": \"string\"," +
                        "  \"selector\": {" +
                        "    \"base\": \"string\"," +
                        "    \"image\": {" +
                        "      \"selector\": \"string\"," +
                        "      \"property\": \"string\"" +
                        "    }," +
                        "    \"price\": {" +
                        "      \"selector\": \"string\"," +
                        "      \"property\": \"string\"" +
                        "    }," +
                        "    \"title\": {" +
                        "      \"selector\": \"string\"," +
                        "      \"property\": \"string\"" +
                        "    }," +
                        "    \"url\": {" +
                        "      \"selector\": \"string\"," +
                        "      \"property\": \"string\"" +
                        "    }" +
                        "  }" +
                        "}"))
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void modifySite() {
        webTestClient.put()
                .uri(API_PREFIX + "/site/{id}", editableSiteId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER, BEARER + adminJwt)
                .body(BodyInserters.fromObject("{" +
                        "  \"categoryIds\": [1]," +
                        "  \"name\": \"string\"," +
                        "  \"url\": \"string\"," +
                        "  \"selector\": {" +
                        "    \"base\": \"string\"," +
                        "    \"image\": {" +
                        "      \"selector\": \"string\"," +
                        "      \"property\": \"string\"" +
                        "    }," +
                        "    \"price\": {" +
                        "      \"selector\": \"string\"," +
                        "      \"property\": \"string\"" +
                        "    }," +
                        "    \"title\": {" +
                        "      \"selector\": \"string\"," +
                        "      \"property\": \"string\"" +
                        "    }," +
                        "    \"url\": {" +
                        "      \"selector\": \"string\"," +
                        "      \"property\": \"string\"" +
                        "    }" +
                        "  }" +
                        "}"))
                .exchange()
                .expectStatus().isAccepted();
    }

    @Test
    void deleteCategory() {
        webTestClient.delete()
                .uri(API_PREFIX + "/site/{id}", deletableSiteId)
                .header(AUTHORIZATION_HEADER, BEARER + adminJwt)
                .exchange()
                .expectStatus().isNoContent();
    }

    private String createSiteAndReturnId() throws JsonProcessingException {
        String responseBody = new String(webTestClient.post()
                .uri(API_PREFIX + "/site")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER, BEARER + adminJwt)
                .body(BodyInserters.fromObject("{" +
                        "  \"categoryIds\": [1]," +
                        "  \"name\": \"string\"," +
                        "  \"url\": \"string\"," +
                        "  \"selector\": {" +
                        "    \"base\": \"string\"," +
                        "    \"image\": {" +
                        "      \"selector\": \"string\"," +
                        "      \"property\": \"string\"" +
                        "    }," +
                        "    \"price\": {" +
                        "      \"selector\": \"string\"," +
                        "      \"property\": \"string\"" +
                        "    }," +
                        "    \"title\": {" +
                        "      \"selector\": \"string\"," +
                        "      \"property\": \"string\"" +
                        "    }," +
                        "    \"url\": {" +
                        "      \"selector\": \"string\"," +
                        "      \"property\": \"string\"" +
                        "    }" +
                        "  }" +
                        "}"))
                .exchange()
                .expectBody()
                .returnResult().getResponseBody());

        return getJsonNode(responseBody, "/id").asText();
    }
}

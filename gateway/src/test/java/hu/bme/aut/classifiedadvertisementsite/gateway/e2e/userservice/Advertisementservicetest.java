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
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.List;

import static hu.bme.aut.classifiedadvertisementsite.gateway.e2e.userservice.util.TestHelper.getJsonNode;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Advertisementservicetest {

    private static final String API_PREFIX = "/api/advertisement";
    private static final String AUTHORIZATION_HEADER = "authorization";
    private static final String BEARER = "bearer ";

    private String adminJwt;
    private String userJwt;
    private Integer editableAdId;
    private Integer deletableAdId;
    private Integer editableCategoryId;
    private Integer deletableCategoryId;
    private Integer deletableCommentId;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JwtUtils jwtUtils;

    @BeforeAll
    void init() throws JsonProcessingException {
        adminJwt = jwtUtils.generateJwtToken(new User(1, "admin", "admin@test.local", List.of("ROLE_ADMIN", "ROLE_USER")));
        userJwt = jwtUtils.generateJwtToken(new User(2, "user", "user@test.local", List.of("ROLE_USER")));
        editableCategoryId = createCategoryAndReturnId();
        deletableCategoryId = createCategoryAndReturnId();
        editableAdId = createAdvertisementAndReturnId();
        deletableAdId = createAdvertisementAndReturnId();
        deletableCommentId = createCommentAndReturnId();
    }

    @Test
    void getAdvertisementById() {
        webTestClient.get()
                .uri(API_PREFIX + "/advertisement/{id}", editableAdId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void modifyAdvertisement() {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("title", "Test");
        bodyBuilder.part("description", "This is a test advertisement");
        bodyBuilder.part("price", 42);
        bodyBuilder.part("categoryId", editableCategoryId);
        bodyBuilder.part("status", "AVAILABLE");

        webTestClient.put()
                .uri(API_PREFIX + "/advertisement/{id}", editableAdId)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER, BEARER + userJwt)
                .body(BodyInserters.fromObject(bodyBuilder.build()))
                .exchange()
                .expectStatus().isAccepted();
    }

    @Test
    void deleteAdvertisement() {
        webTestClient.delete()
                .uri(API_PREFIX + "/advertisement/{id}", deletableAdId)
                .header(AUTHORIZATION_HEADER, BEARER + userJwt)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void createAdvertisement() {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("title", "Test");
        bodyBuilder.part("description", "This is a test advertisement");
        bodyBuilder.part("price", 42);
        bodyBuilder.part("categoryId", editableCategoryId);
        bodyBuilder.part("type", "FIXED_PRICE");

        webTestClient.post()
                .uri(API_PREFIX + "/advertisements")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER, BEARER + userJwt)
                .body(BodyInserters.fromObject(bodyBuilder.build()))
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void getAdvertisementsByCategoryId() {
        webTestClient.get()
                .uri(API_PREFIX + "/advertisements/{categoryId}", editableCategoryId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getNewestAdvertisements() {
        webTestClient.get()
                .uri(API_PREFIX + "/advertisements/new")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void searchAdvertisements() {
        webTestClient.get()
                .uri(API_PREFIX + "/advertisements/search/{query}", "test")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getCommentsByAdvertisementId() {
        webTestClient.get()
                .uri(API_PREFIX + "/advertisement/{id}/comments", editableAdId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void createComment() {
        webTestClient.post()
                .uri(API_PREFIX + "/advertisement/{id}/comments", editableAdId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER, BEARER + userJwt)
                .body(BodyInserters.fromObject("{" +
                        "  \"content\": \"string\"" +
                        "}"))
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void deleteComment() {
        webTestClient.delete()
                .uri(API_PREFIX + "/comment/{id}", deletableCommentId)
                .header(AUTHORIZATION_HEADER, BEARER + userJwt)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void getCategories() {
        webTestClient.get()
                .uri(API_PREFIX + "/categories")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void createCategory() {
        webTestClient.post()
                .uri(API_PREFIX + "/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER, BEARER + adminJwt)
                .body(BodyInserters.fromObject("{" +
                        "  \"name\": \"Test category\"" +
                        "}"))
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void modifyCategory() {
        webTestClient.put()
                .uri(API_PREFIX + "/categories/{id}", editableCategoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER, BEARER + adminJwt)
                .body(BodyInserters.fromObject("{" +
                        "  \"name\": \"Test category\"" +
                        "}"))
                .exchange()
                .expectStatus().isAccepted();
    }

    @Test
    void deleteCategory() {
        webTestClient.delete()
                .uri(API_PREFIX + "/categories/{id}", deletableCategoryId)
                .header(AUTHORIZATION_HEADER, BEARER + adminJwt)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void searchAdvertisementsInCategory() {
        webTestClient.get()
                .uri(API_PREFIX + "/category/{id}/search/{query}", editableCategoryId, "test")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    private Integer createAdvertisementAndReturnId() throws JsonProcessingException {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("title", "Test");
        bodyBuilder.part("description", "This is a test advertisement");
        bodyBuilder.part("price", 42);
        bodyBuilder.part("categoryId", editableCategoryId);
        bodyBuilder.part("type", "FIXED_PRICE");

        String responseBody = new String(webTestClient.post()
                .uri(API_PREFIX + "/advertisements")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER, BEARER + userJwt)
                .body(BodyInserters.fromObject(bodyBuilder.build()))
                .exchange()
                .expectBody()
                .returnResult().getResponseBody());

        return getJsonNode(responseBody, "/id").asInt();
    }

    private Integer createCommentAndReturnId() throws JsonProcessingException {
        String responseBody = new String(webTestClient.post()
                .uri(API_PREFIX + "/advertisement/{id}/comments", editableAdId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER, BEARER + userJwt)
                .body(BodyInserters.fromObject("{" +
                        "  \"content\": \"string\"" +
                        "}"))
                .exchange()
                .expectBody()
                .returnResult().getResponseBody());

        return getJsonNode(responseBody, "/id").asInt();
    }

    private Integer createCategoryAndReturnId() throws JsonProcessingException {
        String responseBody = new String(webTestClient.post()
                .uri(API_PREFIX + "/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER, BEARER + adminJwt)
                .body(BodyInserters.fromObject("{" +
                        "  \"name\": \"Test category\"" +
                        "}"))
                .exchange()
                .expectBody()
                .returnResult().getResponseBody());

        return getJsonNode(responseBody, "/id").asInt();
    }
}

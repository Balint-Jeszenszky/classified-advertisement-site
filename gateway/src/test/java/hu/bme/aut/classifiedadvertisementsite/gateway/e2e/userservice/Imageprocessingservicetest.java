package hu.bme.aut.classifiedadvertisementsite.gateway.e2e.userservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static hu.bme.aut.classifiedadvertisementsite.gateway.e2e.userservice.util.TestHelper.getJsonNode;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Imageprocessingservicetest {
    private static final String API_PREFIX = "/api/images";
    private String imageName;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeAll
    void init() throws JsonProcessingException {
        imageName = getImageName();
    }

    @Test
    void getImagesByAdvertisementId() {
        webTestClient.get()
                .uri(API_PREFIX + "/imageList/{advertisementId}", 1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getThumbnailByAdvertisementId() {
        webTestClient.get()
                .uri(API_PREFIX + "/thumbnail/{advertisementId}", 1)
                .accept(MediaType.IMAGE_JPEG)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getImageByName() {
        webTestClient.get()
                .uri(API_PREFIX + "/image/{name}", imageName)
                .accept(MediaType.IMAGE_JPEG)
                .exchange()
                .expectStatus().isOk();
    }

    private String getImageName() throws JsonProcessingException {
        String responseBody = new String(webTestClient.get()
                .uri(API_PREFIX + "/imageList/{advertisementId}", 1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody()
                .returnResult().getResponseBody());

        return getJsonNode(responseBody, "").get(0).asText();
    }
}

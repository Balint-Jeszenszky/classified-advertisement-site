package hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.integration.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.integration.external.util.MockChannel;
import hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.model.ImageData;
import hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.repository.ImageDataRepository;
import hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.service.messagequeue.ImageProcessingMessageReceiver;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ImageControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ImageDataRepository imageDataRepository;

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private ImageProcessingMessageReceiver receiver;

    @Value("${minio.bucket}")
    private String bucket;

    private static final String TEST_IMAGE_NAME = "test.jpg";

    private static final String MOCK_IMAGE_NAME = "mock.jpg";
    private static final int MOCK_ADVERTISEMENT_ID = 1;

    @BeforeEach
    void setup() throws Exception {
        Resource resource = new ClassPathResource(TEST_IMAGE_NAME);

        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }

        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucket)
                .object("raw/" + TEST_IMAGE_NAME)
                .stream(resource.getInputStream(), resource.contentLength(), -1)
                .build());

        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucket)
                .object("processed/images/" + MOCK_IMAGE_NAME)
                .stream(resource.getInputStream(), resource.contentLength(), -1)
                .build());

        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucket)
                .object("processed/thumbnails/" + MOCK_IMAGE_NAME)
                .stream(resource.getInputStream(), resource.contentLength(), -1)
                .build());

        ImageData imageData = new ImageData(null, MOCK_ADVERTISEMENT_ID, MOCK_IMAGE_NAME, true);

        imageDataRepository.save(imageData);
    }

    @AfterEach
    void cleanUp() throws Exception {
        imageDataRepository.deleteAll();
        Iterable<Result<Item>> images = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucket).recursive(true).build());

        List<DeleteObject> deleteObjects = new ArrayList<>();

        for (Result<Item> image : images) {
            deleteObjects.add(new DeleteObject(image.get().objectName()));
        }

        Iterable<Result<DeleteError>> results = minioClient.removeObjects(RemoveObjectsArgs
                .builder()
                .bucket(bucket)
                .objects(deleteObjects)
                .build());
        for (Result<DeleteError> result : results) {
            result.get();
        }

        minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucket).build());
    }

    @Test
    void getImageByName() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/external/image/" + MOCK_IMAGE_NAME)
                .accept(MediaType.IMAGE_JPEG);

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void getThumbnailByAdvertisementId() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/external/thumbnail/" + MOCK_ADVERTISEMENT_ID)
                .accept(MediaType.IMAGE_JPEG);

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void getImageListByAdvertisementId() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/external/imageList/" + MOCK_ADVERTISEMENT_ID)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect((jsonPath("$").isArray()))
                .andExpect(jsonPath("$[0]").value(MOCK_IMAGE_NAME));
    }

    @Test
    void testImageProcessing() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/external/image/" + TEST_IMAGE_NAME)
                .accept(MediaType.IMAGE_JPEG);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("type", "PROCESS");
        node.put("name", TEST_IMAGE_NAME);
        node.put("advertisementId", 2);
        receiver.onMessage(new Message(node.toString().getBytes()), MockChannel.getMockChannel());

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void testImageDeletion() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/external/image/" + MOCK_IMAGE_NAME)
                .accept(MediaType.IMAGE_JPEG);

        mockMvc.perform(request)
                .andExpect(status().isOk());

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("type", "DELETE");
        ArrayNode arrayNode = mapper.createArrayNode();
        arrayNode.add(MOCK_IMAGE_NAME);
        node.set("imageNames", arrayNode);

        receiver.onMessage(new Message(node.toString().getBytes()), MockChannel.getMockChannel());

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void testAdvertisementDeletion() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/external/image/" + MOCK_IMAGE_NAME)
                .accept(MediaType.IMAGE_JPEG);

        mockMvc.perform(request)
                .andExpect(status().isOk());

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("type", "DELETE_AD");
        node.put("advertisementId", MOCK_ADVERTISEMENT_ID);

        receiver.onMessage(new Message(node.toString().getBytes()), MockChannel.getMockChannel());

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }
}

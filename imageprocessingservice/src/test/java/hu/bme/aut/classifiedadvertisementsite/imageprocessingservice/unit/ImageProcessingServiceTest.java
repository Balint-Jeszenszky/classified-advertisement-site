package hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.unit;

import hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.controller.exceptions.NotFoundException;
import hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.model.ImageData;
import hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.repository.ImageDataRepository;
import hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.service.ImageProcessingService;
import hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.service.ImageProcessingServiceImpl;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.errors.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ImageProcessingServiceTest {
    private MinioClient minioClient;
    private ImageDataRepository imageDataRepository;
    private ImageProcessingService imageProcessingService;

    @BeforeEach
    void init() {
        minioClient = mock(MinioClient.class);
        imageDataRepository = mock(ImageDataRepository.class);

        imageProcessingService = new ImageProcessingServiceImpl(
                imageDataRepository,
                minioClient,
                "test",
                "watermark"
        );
    }

    @Test
    void getImageByName_ThrowsNotFoundException() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        when(minioClient.getObject(any(GetObjectArgs.class))).thenThrow(new IOException());

        assertThrows(NotFoundException.class, () -> imageProcessingService.getImageByName("name"));
    }

    @Test
    void getImageByName_Successful() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        GetObjectResponse getObjectResponse = new GetObjectResponse(
                null,
                "bucket",
                "region",
                "obj",
                null);

        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(getObjectResponse);

        assertTrue(imageProcessingService.getImageByName("name").isReadable());
    }

    @Test
    void getThumbnailByName_ThrowsNotFoundException() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        when(minioClient.getObject(any(GetObjectArgs.class))).thenThrow(new IOException());

        assertThrows(NotFoundException.class, () -> imageProcessingService.getThumbnailByName("name"));
    }

    @Test
    void getThumbnailByName_Successful() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        GetObjectResponse getObjectResponse = new GetObjectResponse(
                null,
                "bucket",
                "region",
                "obj",
                null);

        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(getObjectResponse);

        assertTrue(imageProcessingService.getThumbnailByName("name").isReadable());
    }

    @Test
    void deleteImagesByName() {
        List<String> imageNames = List.of("image1", "image2");
        List<ImageData> images = List.of(
                new ImageData(1, 1, "image1", true),
                new ImageData(2, 1 , "image2", false));
        when(imageDataRepository.findAllByNameIn(imageNames)).thenReturn(images);
        when(imageDataRepository.findAllByAdvertisementId(1)).thenReturn(List.of());

        imageProcessingService.deleteImagesByName(imageNames);

        verify(imageDataRepository).deleteAll(images);
        verify(minioClient, times(2)).removeObjects(any());
    }

    @Test
    void deleteImagesByAd() {
        List<ImageData> images = List.of(
                new ImageData(1, 1, "image1", true),
                new ImageData(2, 1 , "image2", false));
        when(imageDataRepository.findAllByAdvertisementId(1)).thenReturn(images);

        imageProcessingService.deleteImagesByAdvertisementId(1);

        verify(imageDataRepository).deleteAll(images);
        verify(minioClient, times(2)).removeObjects(any());
    }
}

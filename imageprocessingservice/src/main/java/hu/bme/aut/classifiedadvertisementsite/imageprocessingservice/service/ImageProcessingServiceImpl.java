package hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.service;

import hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.model.ImageData;
import hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.repository.ImageDataRepository;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.RemoveObjectsArgs;
import io.minio.errors.*;
import io.minio.messages.DeleteObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public class ImageProcessingServiceImpl implements ImageProcessingService {
    private static final String IMAGE_PATH = "processed/images/";
    private static final String THUMBNAIL_PATH = "processed/thumbnails/";
    private static final String RAW_PATH = "raw/";
    private String bucket;
    private MinioClient minioClient;
    private ImageDataRepository imageDataRepository;

    ImageProcessingServiceImpl(
            ImageDataRepository imageDataRepository,
            @Value("${minio.endpoint}") String endpoint,
            @Value("${minio.username}") String username,
            @Value("${minio.password}") String password,
            @Value("${minio.bucket}") String bucket) {
        this.imageDataRepository = imageDataRepository;
        this.bucket = bucket;
        minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(username, password)
                .build();
    }

    @Override
    public Resource getImageByName(String name) {
        GetObjectResponse object;
        try {
            object = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(IMAGE_PATH + name)
                            .build());
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e); // TODO
        }

        return new InputStreamResource(object);
    }

    @Override
    public Resource getThumbnailByName(String name) {
        return null;
    }

    @Override
    public void deleteImagesById(List<Integer> imageIds) {
        List<ImageData> images = imageDataRepository.findAllById(imageIds);

        removeImages(images.stream().map(i -> new DeleteObject(IMAGE_PATH + i.getName())).toList());
        removeImages(images
                .stream()
                .filter(ImageData::getThumbnail)
                .map(i -> new DeleteObject(IMAGE_PATH + i.getName()))
                .toList());

        // TODO create new thumbnail if needed

        imageDataRepository.deleteAll(images);
    }

    @Override
    public void deleteImagesByAdvertisementId(int advertisementId) {
        List<ImageData> images = imageDataRepository.findAllByAdvertisementId(advertisementId);

        removeImages(images.stream().map(i -> new DeleteObject(IMAGE_PATH + i.getName())).toList());
        removeImages(images.stream().map(i -> new DeleteObject(THUMBNAIL_PATH + i.getName())).toList());

        imageDataRepository.deleteAll(images);
    }

    private void removeImages(List<DeleteObject> images) {
        minioClient.removeObjects(RemoveObjectsArgs.builder().bucket(bucket).objects(images).build());
    }

    @Override
    public void processImage(String name, int advertisementId) {

    }
}

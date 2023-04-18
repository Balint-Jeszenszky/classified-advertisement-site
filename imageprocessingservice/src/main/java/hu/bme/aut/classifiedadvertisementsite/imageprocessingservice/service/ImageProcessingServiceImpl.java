package hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.service;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class ImageProcessingServiceImpl implements ImageProcessingService {
    private String bucket;
    private MinioClient minioClient;

    ImageProcessingServiceImpl(
            @Value("${minio.endpoint}") String endpoint,
            @Value("${minio.username}") String username,
            @Value("${minio.password}") String password,
            @Value("${minio.bucket}") String bucket) {
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
                            .object("processed/images/" + name)
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
}

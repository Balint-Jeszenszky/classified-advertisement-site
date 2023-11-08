package hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.service;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Minio {

    @Bean
    public MinioClient getMinioClient(
            @Value("${minio.endpoint}") String endpoint,
            @Value("${minio.username}") String username,
            @Value("${minio.password}") String password) {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(username, password)
                .build();
    }
}

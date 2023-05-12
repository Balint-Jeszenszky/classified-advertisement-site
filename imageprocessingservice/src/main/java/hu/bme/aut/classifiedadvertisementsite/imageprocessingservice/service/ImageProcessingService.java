package hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.service;

import org.springframework.core.io.Resource;

import java.util.List;

public interface ImageProcessingService {
    Resource getImageByName(String name);
    Resource getThumbnailByName(String name);
    void deleteImagesByName(List<String> imageNames);
    void deleteImagesByAdvertisementId(int advertisementId);
    void processImage(String name, int advertisementId) throws Exception;
}

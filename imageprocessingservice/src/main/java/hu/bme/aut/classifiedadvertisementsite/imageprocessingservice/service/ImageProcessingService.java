package hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.service;

import org.springframework.core.io.Resource;

public interface ImageProcessingService {
    Resource getImageByName(String name);
    Resource getThumbnailByName(String name);
}

package hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.service;

import hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.model.ImageData;
import hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.repository.ImageDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {
    private final ImageDataRepository imageDataRepository;
    private final ImageProcessingService imageProcessingService;

    public List<String> getImageListByAdvertisementId(Integer advertisementId) {
        return imageDataRepository.findAllByAdvertisementId(advertisementId).stream().map(ImageData::getName).toList();
    }

    public Resource getImageByName(String name) {
        return imageProcessingService.getImageByName(name);
    }

    public Resource getThumbnailByAdvertisementId(Integer advertisementId) {
        return new FileSystemResource(new File(""));
    }
}

package hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.service;

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
    public List<String> getImageListByAdcertisementId(Integer advertisementId) {
        return List.of();
    }

    public Resource getImageByName(String name) {
        return new FileSystemResource(new File(""));
    }

    public Resource getThumbnailByAdvertisementId(Integer advertisementId) {
        return new FileSystemResource(new File(""));
    }
}

package hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.controller.external;

import hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.api.external.ImagesApi;
import hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ImageController implements ExternalApi, ImagesApi {
    private final ImageService imageService;

    @Override
    public ResponseEntity<List<String>> getImageListAdvertisementId(Integer advertisementId) {
        List<String> imageList = imageService.getImageListByAdvertisementId(advertisementId);
        return new ResponseEntity<>(imageList, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Resource> getImageName(String name) {
        Resource image = imageService.getImageByName(name);
        return new ResponseEntity<>(image, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Resource> getThumbnailAdvertisementId(Integer advertisementId) {
        Resource image = imageService.getThumbnailByAdvertisementId(advertisementId);
        return new ResponseEntity<>(image, HttpStatus.OK);
    }
}

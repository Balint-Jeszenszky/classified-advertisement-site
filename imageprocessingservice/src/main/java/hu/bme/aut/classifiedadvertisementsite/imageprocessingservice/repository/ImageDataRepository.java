package hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.repository;

import hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.model.ImageData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageDataRepository extends JpaRepository<ImageData, Integer> {
    List<ImageData> findAllByAdvertisementId(Integer id);

    Integer countByAdvertisementId(Integer id);

    List<ImageData> findAllByNameIn(List<String> names);

    Optional<ImageData> findByAdvertisementIdAndThumbnailIsTrue(Integer id);
}

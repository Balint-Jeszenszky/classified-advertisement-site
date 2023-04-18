package hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.repository;

import hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.model.ImageData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageDataRepository extends JpaRepository<ImageData, Integer> {
    List<ImageData> findAllByAdvertisementId(Integer id);
}

package hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.repository;

import hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.model.ImageData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageDataRepository extends JpaRepository<ImageData, Integer> {
}

package hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "ImageData")
@Table(name = "image_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageData {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "image_data_seq")
    @SequenceGenerator(name = "image_data_seq", sequenceName = "image_data_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private Integer id;

    @Column(name = "advertisement_id", nullable = false, updatable = false)
    private Integer advertisementId;

    @Column(name = "name", unique = true, updatable = false, nullable = false)
    private String name;
}

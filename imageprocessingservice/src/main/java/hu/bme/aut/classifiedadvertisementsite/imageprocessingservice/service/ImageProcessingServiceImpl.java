package hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.service;

import hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.controller.exceptions.NotFoundException;
import hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.model.ImageData;
import hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.repository.ImageDataRepository;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.FileNameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

import static java.lang.Math.max;

@Service
@Slf4j
public class ImageProcessingServiceImpl implements ImageProcessingService {
    private static final String IMAGE_PATH = "processed/images/";
    private static final String THUMBNAIL_PATH = "processed/thumbnails/";
    private static final String RAW_PATH = "raw/";
    private static final String JPG_EXTENSION = ".jpg";
    private final String bucket;
    private final MinioClient minioClient;
    private final ImageDataRepository imageDataRepository;
    private final String watermarkText;

    ImageProcessingServiceImpl(
            ImageDataRepository imageDataRepository,
            @Value("${minio.endpoint}") String endpoint,
            @Value("${minio.username}") String username,
            @Value("${minio.password}") String password,
            @Value("${minio.bucket}") String bucket,
            @Value("advertisement.image.watermark.text") String watermarkText) {
        this.imageDataRepository = imageDataRepository;
        this.bucket = bucket;
        this.watermarkText = watermarkText;
        minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(username, password)
                .build();
    }

    @Override
    public Resource getImageByName(String name) {
        return getImageByPath(IMAGE_PATH + name);
    }

    @Override
    public Resource getThumbnailByName(String name) {
        return getImageByPath(THUMBNAIL_PATH + name);
    }

    private Resource getImageByPath(String path) {
        GetObjectResponse object;
        try {
            object = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(path)
                            .build());
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            throw new NotFoundException("Image not found");
        }

        return new InputStreamResource(object);
    }

    @Override
    public void deleteImagesById(List<Integer> imageIds) {
        List<ImageData> images = imageDataRepository.findAllById(imageIds);

        try {
            removeImages(images.stream().map(i -> new DeleteObject(IMAGE_PATH + i.getName())).toList());
            removeImages(images
                    .stream()
                    .filter(ImageData::getThumbnail)
                    .map(i -> new DeleteObject(THUMBNAIL_PATH + i.getName()))
                    .toList());
        } catch (Exception e) {
            log.error("Error when deleting images by ids {}", imageIds);
            log.error(e.getMessage());
        }

        imageDataRepository.deleteAll(images);

        for (ImageData imageData : images.stream().filter(ImageData::getThumbnail).toList()) {
            Optional<ImageData> first = imageDataRepository.findAllByAdvertisementId(imageData.getAdvertisementId()).stream().findFirst();
            if (first.isPresent()) {
                ImageData image = first.get();

                try {
                    convertImage(getImageByPath(IMAGE_PATH + image.getName()), 100, THUMBNAIL_PATH + image.getName(), false);
                    image.setThumbnail(true);
                    imageDataRepository.save(image);
                } catch (Exception e) {
                    log.error("Creating thumbnail after image deletion failed for advertisement {}, image {}", image.getAdvertisementId(), image.getName());
                }
            }
        }
    }

    @Override
    public void deleteImagesByAdvertisementId(int advertisementId) {
        List<ImageData> images = imageDataRepository.findAllByAdvertisementId(advertisementId);

        try {
            removeImages(images.stream().map(i -> new DeleteObject(IMAGE_PATH + i.getName())).toList());
            removeImages(images.stream().map(i -> new DeleteObject(THUMBNAIL_PATH + i.getName())).toList());
        } catch (Exception e) {
            log.error("Error when deleting images by advertisement {}", advertisementId);
            log.error(e.getMessage());
        }

        imageDataRepository.deleteAll(images);
    }

    private void removeImages(List<DeleteObject> images) throws Exception {
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(RemoveObjectsArgs
                .builder()
                .bucket(bucket)
                .objects(images)
                .build());
        for (Result<DeleteError> result : results) {
            DeleteError error = result.get();
            log.error("Error in deleting object " + error.objectName() + "; " + error.message());
        }
    }

    @Override
    public void processImage(String name, int advertisementId) throws Exception {
        String newName = FileNameUtils.getBaseName(name) + JPG_EXTENSION;

        convertImage(getImageByPath(RAW_PATH + name), 1000, IMAGE_PATH + newName, true);

        ImageData imageData = new ImageData();
        imageData.setName(newName);
        imageData.setAdvertisementId(advertisementId);

        if (imageDataRepository.findByAdvertisementIdAndThumbnailIsTrue(advertisementId).isEmpty()) {
            convertImage(getImageByPath(RAW_PATH + name), 100, THUMBNAIL_PATH + newName, false);
            imageData.setThumbnail(Boolean.TRUE);
        }

        removeImages(List.of(new DeleteObject(RAW_PATH + name)));

        imageDataRepository.save(imageData);
    }

    private void convertImage(Resource image, int size, String path, boolean watermark) throws Exception {
        BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
        double scale = (double)size / max(bufferedImage.getHeight(), bufferedImage.getWidth());
        int width = (int)(bufferedImage.getWidth() * scale);
        int height = (int)(bufferedImage.getHeight() * scale);
        Image resultingImage = bufferedImage.getScaledInstance(width, height, Image.SCALE_DEFAULT);
        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bufferedImage.getGraphics();
        graphics.drawImage(resultingImage, 0, 0, null);
        if (watermark) {
            graphics.setColor(new Color(255, 255, 255, 128));
            graphics.setFont(new Font("Roboto", Font.PLAIN, 80));
            graphics.drawString("Watermark", 10, height);
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpeg", os);
        InputStream is = new ByteArrayInputStream(os.toByteArray());

        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucket)
                .object(path)
                .stream(is, os.size(), -1)
                .build());
    }
}

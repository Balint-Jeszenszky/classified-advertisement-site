package hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.service;

import hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.model.ImageData;
import hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.repository.ImageDataRepository;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
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

import static java.lang.Math.max;

@Service
public class ImageProcessingServiceImpl implements ImageProcessingService {
    private static final String IMAGE_PATH = "processed/images/";
    private static final String THUMBNAIL_PATH = "processed/thumbnails/";
    private static final String RAW_PATH = "raw/";
    private static final String JPG_EXTENSION = ".jpg";
    private String bucket;
    private MinioClient minioClient;
    private ImageDataRepository imageDataRepository;

    ImageProcessingServiceImpl(
            ImageDataRepository imageDataRepository,
            @Value("${minio.endpoint}") String endpoint,
            @Value("${minio.username}") String username,
            @Value("${minio.password}") String password,
            @Value("${minio.bucket}") String bucket) {
        this.imageDataRepository = imageDataRepository;
        this.bucket = bucket;
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
            throw new RuntimeException(e); // TODO
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
                    .map(i -> new DeleteObject(IMAGE_PATH + i.getName()))
                    .toList());
        } catch (Exception e) {
            throw new RuntimeException(); // TODO
        }

        // TODO create new thumbnail if needed

        imageDataRepository.deleteAll(images);
    }

    @Override
    public void deleteImagesByAdvertisementId(int advertisementId) {
        List<ImageData> images = imageDataRepository.findAllByAdvertisementId(advertisementId);

        try {
            removeImages(images.stream().map(i -> new DeleteObject(IMAGE_PATH + i.getName())).toList());
            removeImages(images.stream().map(i -> new DeleteObject(THUMBNAIL_PATH + i.getName())).toList());
        } catch (Exception e) {
            throw new RuntimeException(); // TODO
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
            System.out.println("Error in deleting object " + error.objectName() + "; " + error.message());
        }
    }

    @Override
    public void processImage(String name, int advertisementId) throws Exception {
        String newName = FileNameUtils.getBaseName(name) + JPG_EXTENSION;

        convertImage(getImageByPath(RAW_PATH + name), 600, IMAGE_PATH + newName, true);

        ImageData imageData = new ImageData();
        imageData.setName(newName);
        imageData.setAdvertisementId(advertisementId);

        if (imageDataRepository.findByAdvertisementIdAndThumbnailIsTrue(advertisementId).isEmpty()) {
            convertImage(getImageByPath(RAW_PATH + name), 50, THUMBNAIL_PATH + newName, false);
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
        bufferedImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        if (watermark) {
            bufferedImage.getGraphics().drawString("Watermark", 10, height - 50);
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
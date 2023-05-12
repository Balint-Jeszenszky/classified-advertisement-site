package hu.bme.aut.classifiedadvertisementsite.advertisementservice.service.mock

import hu.bme.aut.classifiedadvertisementsite.advertisementservice.service.FileUploadService
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
@Profile("test")
class MockFileUploadService : FileUploadService {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun uploadFiles(files: MutableList<MultipartFile>, advertisementId: Int) {
        log.info("Upload {} files: {}, ad: {}", files.size, files.map { it.originalFilename }, advertisementId)
    }

    override fun deleteImagesForAd(advertisementId: Int) {
        log.info("Delete images for ad: {}", advertisementId)
    }

    override fun deleteImagesByName(deletedImages: List<String>) {
        log.info("Delete images: {}", deletedImages)
    }
}
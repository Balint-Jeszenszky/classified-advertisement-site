package hu.bme.aut.classifiedadvertisementsite.advertisementservice.service

import org.springframework.web.multipart.MultipartFile

interface FileUploadService {
    fun uploadFiles(files: MutableList<MultipartFile>, advertisementId: Int)
    fun deleteImagesForAd(advertisementId: Int)
    fun deleteImagesByName(deletedImages: List<String>)
}
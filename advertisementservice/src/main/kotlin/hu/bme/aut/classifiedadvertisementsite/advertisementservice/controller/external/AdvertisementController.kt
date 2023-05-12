package hu.bme.aut.classifiedadvertisementsite.advertisementservice.controller.external

import hu.bme.aut.classifiedadvertisementsite.advertisementservice.java.api.external.AdvertisementApi
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.java.api.external.model.AdvertisementResponse
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.java.api.external.model.NewAdvertisementsResponse
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.service.AdvertisementService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class AdvertisementController(
    private val advertisementService: AdvertisementService
) : ExternalApi, AdvertisementApi {

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    override fun deleteAdvertisementId(id: Int): ResponseEntity<Void> {
        advertisementService.deleteById(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    override fun getAdvertisementId(id: Int): ResponseEntity<AdvertisementResponse> {
        val advertisementResponse: AdvertisementResponse = advertisementService.getAdvertisementById(id)
        return ResponseEntity(advertisementResponse, HttpStatus.OK)
    }

    override fun getAdvertisements(categoryId: Int): ResponseEntity<List<AdvertisementResponse>> {
        val advertisements: List<AdvertisementResponse> = advertisementService.getAdvertisementsByCategory(categoryId)
        return ResponseEntity(advertisements, HttpStatus.OK)
    }

    override fun getAdvertisementsNew(): ResponseEntity<List<NewAdvertisementsResponse>> {
        val advertisements: List<NewAdvertisementsResponse> = advertisementService.getNewestAdvertisements()
        return ResponseEntity(advertisements, HttpStatus.OK)
    }

    override fun getAdvertisementsSearchQuery(query: String): ResponseEntity<List<AdvertisementResponse>> {
        val advertisements: List<AdvertisementResponse> = advertisementService.search(query)
        return ResponseEntity(advertisements, HttpStatus.OK)
    }

    override fun getCategoryIdSearchQuery(id: Int, query: String): ResponseEntity<List<AdvertisementResponse>> {
        val advertisements: List<AdvertisementResponse> = advertisementService.searchByCategoryId(id, query)
        return ResponseEntity(advertisements, HttpStatus.OK)
    }

    @PreAuthorize("hasRole('USER')")
    override fun postAdvertisements(
        title: String,
        description: String,
        price: Double,
        categoryId: Int,
        images: MutableList<MultipartFile>?
    ): ResponseEntity<AdvertisementResponse> {
        val advertisement: AdvertisementResponse = advertisementService.createAdvertisement(
            title,
            description,
            price,
            categoryId,
            images)
        return ResponseEntity(advertisement, HttpStatus.CREATED)
    }

    @PreAuthorize("hasRole('USER')")
    override fun putAdvertisementId(
        id: Int,
        title: String,
        description: String,
        price: Double,
        categoryId: Int,
        status: String,
        images: MutableList<MultipartFile>?,
        deletedImages: String?
    ): ResponseEntity<AdvertisementResponse> {
        val advertisement = advertisementService.updateAdvertisement(
            id,
            title,
            description,
            price,
            categoryId,
            status,
            images,
            deletedImages)
        return ResponseEntity(advertisement, HttpStatus.ACCEPTED)
    }
}
package hu.bme.aut.classifiedadvertisementsite.advertisementservice.controller.external

import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.external.AdvertisementApi
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.external.model.AdvertisementRequest
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.external.model.AdvertisementResponse
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.controller.exception.BadRequestException
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.service.AdvertisementService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class AdvertisementController(
    private val advertisementService: AdvertisementService
) : ExternalApi, AdvertisementApi {

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    override fun deleteAdvertisementId(id: Int): ResponseEntity<Unit> {
        advertisementService.deleteById(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    override fun getAdvertisementId(id: Int): ResponseEntity<AdvertisementResponse> {
        val advertisementResponse: AdvertisementResponse = advertisementService.getAdvertisementById(id)
        return ResponseEntity(advertisementResponse, HttpStatus.OK)
    }

    override fun getAdvertisements(): ResponseEntity<List<AdvertisementResponse>> {
        val advertisements: List<AdvertisementResponse> = advertisementService.getAllAdvertisements()
        return ResponseEntity(advertisements, HttpStatus.OK)
    }

    @PreAuthorize("hasRole('USER')")
    override fun postAdvertisements(advertisementRequest: AdvertisementRequest?): ResponseEntity<AdvertisementResponse> {
        if (advertisementRequest == null) {
            throw BadRequestException("Invalid advertisement")
        }

        val advertisement: AdvertisementResponse = advertisementService.createAdvertisement(advertisementRequest)
        return ResponseEntity(advertisement, HttpStatus.CREATED)
    }

    @PreAuthorize("hasRole('USER')")
    override fun putAdvertisementId(
        id: Int,
        advertisementRequest: AdvertisementRequest?
    ): ResponseEntity<AdvertisementResponse> {
        if (advertisementRequest == null) {
            throw BadRequestException("Invalid advertisement")
        }

        val advertisement = advertisementService.updateAdvertisement(id, advertisementRequest)
        return ResponseEntity(advertisement, HttpStatus.ACCEPTED)
    }
}
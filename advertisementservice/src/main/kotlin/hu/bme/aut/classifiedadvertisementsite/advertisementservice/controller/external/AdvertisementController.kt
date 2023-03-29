package hu.bme.aut.classifiedadvertisementsite.advertisementservice.controller.external

import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.external.AdvertisementApi
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.external.model.AdvertisementResponse
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.service.AdvertisementService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class AdvertisementController(
    private val advertisementService: AdvertisementService
) : ExternalApi, AdvertisementApi {

    override fun getAdvertisementId(id: Int): ResponseEntity<AdvertisementResponse> {
        val advertisementResponse: AdvertisementResponse = advertisementService.getAdvertisementById(id)
        return ResponseEntity(advertisementResponse, HttpStatus.OK)
    }
}
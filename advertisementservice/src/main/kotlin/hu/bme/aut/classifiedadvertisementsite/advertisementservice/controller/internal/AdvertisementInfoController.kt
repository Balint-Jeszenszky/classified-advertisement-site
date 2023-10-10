package hu.bme.aut.classifiedadvertisementsite.advertisementservice.controller.internal

import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.internal.AdvertisementApi
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.internal.model.AdvertisementExistsResponse
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.service.AdvertisementService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class AdvertisementInfoController(
    private val advertisementService: AdvertisementService,
) : InternalApi, AdvertisementApi {
    override fun getAdvertisementExistsId(id: Int): ResponseEntity<AdvertisementExistsResponse> {
        val advertisement = advertisementService.existsById(id)

        return ResponseEntity(advertisement, HttpStatus.OK)
    }

}
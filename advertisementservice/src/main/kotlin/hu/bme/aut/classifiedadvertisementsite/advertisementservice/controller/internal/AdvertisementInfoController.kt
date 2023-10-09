package hu.bme.aut.classifiedadvertisementsite.advertisementservice.controller.internal

import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.internal.AdvertisementApi
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.service.AdvertisementService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class AdvertisementInfoController(
    private val advertisementService: AdvertisementService,
) : InternalApi, AdvertisementApi {
    override fun headExistsId(id: Int): ResponseEntity<Unit> {
        return if (advertisementService.existsById(id)) {
            ResponseEntity(HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }
}
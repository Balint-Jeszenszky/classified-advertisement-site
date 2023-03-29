package hu.bme.aut.classifiedadvertisementsite.advertisementservice.service

import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.external.model.AdvertisementResponse
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.controller.exception.NotFoundException
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.mapper.AdvertisementMapper
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.repository.AdvertisementRepository
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service

@Service
class AdvertisementService(
    private val advertisementRepository: AdvertisementRepository
) {
    private val advertisementMapper: AdvertisementMapper = Mappers.getMapper(AdvertisementMapper::class.java)

    fun getAdvertisementById(id: Int): AdvertisementResponse {
        val advertisement = advertisementRepository.findById(id).orElseThrow { NotFoundException("Advertisement not found") }
        return advertisementMapper.advertisementToAdvertisementResponse(advertisement)
    }
}
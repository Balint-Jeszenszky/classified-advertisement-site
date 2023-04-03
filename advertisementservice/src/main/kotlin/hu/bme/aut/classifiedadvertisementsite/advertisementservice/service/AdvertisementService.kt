package hu.bme.aut.classifiedadvertisementsite.advertisementservice.service

import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.external.model.AdvertisementRequest
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.external.model.AdvertisementResponse
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.controller.exception.ForbiddenException
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.controller.exception.NotFoundException
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.mapper.AdvertisementMapper
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.Advertisement
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.repository.AdvertisementRepository
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.security.LoggedInUserService
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class AdvertisementService(
    private val advertisementRepository: AdvertisementRepository,
    private val loggedInUserService: LoggedInUserService
) {
    private val advertisementMapper: AdvertisementMapper = Mappers.getMapper(AdvertisementMapper::class.java)

    fun getAdvertisementById(id: Int): AdvertisementResponse {
        val advertisement = advertisementRepository.findById(id).orElseThrow { NotFoundException("Advertisement not found") }
        return advertisementMapper.advertisementToAdvertisementResponse(advertisement)
    }

    fun getAllAdvertisements(): List<AdvertisementResponse> {
        val advertisements = advertisementRepository.findAll()
        return advertisements.map { advertisementMapper.advertisementToAdvertisementResponse(it) }
    }

    fun createAdvertisement(advertisementRequest: AdvertisementRequest): AdvertisementResponse {
        val user = loggedInUserService.getLoggedInUser() ?: throw ForbiddenException("User not found")

        val advertisement = Advertisement(
            advertisementRequest.title,
            advertisementRequest.description,
            user.getId(),
            advertisementRequest.price.toDouble())

        advertisementRepository.save(advertisement)

        return advertisementMapper.advertisementToAdvertisementResponse(advertisement)
    }

    fun deleteById(id: Int) {
        val user = loggedInUserService.getLoggedInUser() ?: throw ForbiddenException("User not found")
        val advertisement = advertisementRepository.findById(id)
            .orElseThrow { ForbiddenException("Advertisement not found") }

        if (!loggedInUserService.isAdmin() && advertisement.advertiserId != user.getId()) {
            throw ForbiddenException("Can not delete advertisement")
        }

        advertisementRepository.delete(advertisement)
    }

    fun updateAdvertisement(id: Int, advertisementRequest: AdvertisementRequest): AdvertisementResponse {
        val user = loggedInUserService.getLoggedInUser() ?: throw ForbiddenException("User not found")
        val advertisement = advertisementRepository.findByIdAndAdvertiserId(id, user.getId())
            .orElseThrow { ForbiddenException("Advertisement not found") }

        advertisement.title = advertisementRequest.title
        advertisement.description = advertisementRequest.description
        advertisement.price = advertisementRequest.price.toDouble()
        advertisement.updatedAt = OffsetDateTime.now()

        advertisementRepository.save(advertisement)

        return advertisementMapper.advertisementToAdvertisementResponse(advertisement)
    }
}
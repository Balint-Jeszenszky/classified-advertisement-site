package hu.bme.aut.classifiedadvertisementsite.advertisementservice.service

import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.external.model.AdvertisementRequest
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.external.model.AdvertisementResponse
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.external.model.NewAdvertisementsResponse
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.controller.exception.BadRequestException
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.controller.exception.ForbiddenException
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.controller.exception.NotFoundException
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.mapper.AdvertisementMapper
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.mapper.CategoryMapper
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.Advertisement
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.AdvertisementStatus
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.Category
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.repository.AdvertisementRepository
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.repository.CategoryRepository
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.security.LoggedInUserService
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class AdvertisementService(
    private val advertisementRepository: AdvertisementRepository,
    private val loggedInUserService: LoggedInUserService,
    private val categoryRepository: CategoryRepository,
    private val fileUploadService: FileUploadService
) {
    private val advertisementMapper: AdvertisementMapper = Mappers.getMapper(AdvertisementMapper::class.java)
    private val categoryMapper: CategoryMapper = Mappers.getMapper(CategoryMapper::class.java)

    fun getAdvertisementById(id: Int): AdvertisementResponse {
        val advertisement = advertisementRepository.findById(id).orElseThrow { NotFoundException("Advertisement not found") }
        return advertisementMapper.advertisementToAdvertisementResponse(advertisement)
    }

    fun getAdvertisementsByCategory(categoryId: Int): List<AdvertisementResponse> {
        val category = categoryRepository.findById(categoryId).orElseThrow { NotFoundException("Category not found") }
        val categories : List<Category> = getSubcategories(category)
        val advertisements = advertisementRepository.findByCategoryIn(categories)
        return advertisements.map { advertisementMapper.advertisementToAdvertisementResponse(it) }
    }

    private fun getSubcategories(category: Category): List<Category> {
        val subcategories = category.childrenCategory.map {
            getSubcategories(it)
        }.flatten()
        return listOf(category, *subcategories.toTypedArray())
    }

    fun createAdvertisement(advertisementRequest: AdvertisementRequest): AdvertisementResponse {
        val user = loggedInUserService.getLoggedInUser() ?: throw ForbiddenException("User not found")
        val category = categoryRepository.findById(advertisementRequest.categoryId)
            .orElseThrow { BadRequestException("Category not found") }

        val advertisement = Advertisement(
            advertisementRequest.title,
            advertisementRequest.description,
            user.getId(),
            advertisementRequest.price,
            category,
            AdvertisementStatus.AVAILABLE)

        advertisementRepository.save(advertisement)

//        fileUploadService.uploadFile(advertisementRequest.image, advertisement.id!!)

        return advertisementMapper.advertisementToAdvertisementResponse(advertisement)
    }

    fun deleteById(id: Int) {
        val user = loggedInUserService.getLoggedInUser() ?: throw ForbiddenException("User not found")
        val advertisement = advertisementRepository.findById(id)
            .orElseThrow { ForbiddenException("Advertisement not found") }

        if (!loggedInUserService.isAdmin() && advertisement.advertiserId != user.getId()) {
            throw ForbiddenException("Can not delete advertisement")
        }

        fileUploadService.deleteImagesForAd(id)
        advertisementRepository.delete(advertisement)
    }

    fun updateAdvertisement(id: Int, advertisementRequest: AdvertisementRequest): AdvertisementResponse {
        val user = loggedInUserService.getLoggedInUser() ?: throw ForbiddenException("User not found")
        val advertisement = advertisementRepository.findByIdAndAdvertiserId(id, user.getId())
            .orElseThrow { ForbiddenException("Advertisement not found") }
        val category = categoryRepository.findById(advertisementRequest.categoryId)
            .orElseThrow { BadRequestException("Category not found") }

        advertisement.title = advertisementRequest.title
        advertisement.description = advertisementRequest.description
        advertisement.price = advertisementRequest.price
        advertisement.updatedAt = OffsetDateTime.now()
        advertisement.category = category
        if (advertisementRequest.status != null && advertisement.status.value != advertisementRequest.status.value) {
            val newStatus = AdvertisementStatus.valueOf(advertisementRequest.status.value)
            if (!validStateTransition(advertisement.status, newStatus)) {
                throw BadRequestException("Invalid status transition")
            }
            advertisement.status = newStatus
        }

        advertisementRepository.save(advertisement)

        return advertisementMapper.advertisementToAdvertisementResponse(advertisement)
    }

    private fun validStateTransition(from: AdvertisementStatus, to: AdvertisementStatus): Boolean {
        return when (from) {
            AdvertisementStatus.AVAILABLE -> true
            AdvertisementStatus.FREEZED -> listOf(AdvertisementStatus.AVAILABLE, AdvertisementStatus.SOLD).contains(to)
            else -> false
        }
    }

    fun searchByCategoryId(id: Int, query: String): List<AdvertisementResponse> {
        val category = categoryRepository.findById(id).orElseThrow { NotFoundException("Category not found") }
        val categories : List<Category> = getSubcategories(category)
        val advertisements = advertisementRepository.findByCategoryInAndTitleContainsOrDescriptionContains(categories, query)
        return advertisements.map { advertisementMapper.advertisementToAdvertisementResponse(it) }
    }

    fun getNewestAdvertisements(): List<NewAdvertisementsResponse> {
        val newAdvertisements = advertisementRepository.getNewAdvertisements(5)
        return newAdvertisements.groupBy {
            it.category
        }.map {
            NewAdvertisementsResponse(
                categoryMapper.categoryToCategoryResponse(it.key),
                it.value.map {
                    advertisementMapper.advertisementToAdvertisementResponse(it)
                })
        }
    }

    fun search(query: String): List<AdvertisementResponse> {
        val advertisements = advertisementRepository.findByTitleContainsOrDescriptionContains(query, query)
        return advertisements.map { advertisementMapper.advertisementToAdvertisementResponse(it) }
    }
}
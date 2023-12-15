package hu.bme.aut.classifiedadvertisementsite.advertisementservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.internal.model.AdvertisementExistsResponse
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.client.java.api.model.CreateBidRequest
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.client.java.api.model.ModifyBidRequest
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.controller.exception.*
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.java.api.external.model.AdvertisementResponse
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.java.api.external.model.NewAdvertisementsResponse
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.java.api.external.model.AdvertisementDataResponse
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.mapper.AdvertisementMapper
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.mapper.CategoryMapper
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.Advertisement
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.AdvertisementStatus
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.AdvertisementType
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.Category
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.repository.AdvertisementRepository
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.repository.CategoryRepository
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.security.LoggedInUserService
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.service.apiclient.BidApiClient
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.service.messagequeue.AdvertisementMessageQueue
import jakarta.transaction.Transactional
import org.mapstruct.factory.Mappers
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import org.springframework.web.multipart.MultipartFile
import java.time.OffsetDateTime

@Service
class AdvertisementService(
    private val advertisementRepository: AdvertisementRepository,
    private val loggedInUserService: LoggedInUserService,
    private val categoryRepository: CategoryRepository,
    private val fileUploadService: FileUploadService,
    private val rabbitTemplate: RabbitTemplate,
    @Qualifier(AdvertisementMessageQueue.QUEUE_NAME) private val queue: Queue,
    private val bidApiClient: BidApiClient,
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

    @Transactional
    fun createAdvertisement(
        title: String,
        description: String,
        price: Double,
        categoryId: Int,
        type: String,
        expiration: OffsetDateTime?,
        images: MutableList<MultipartFile>?
    ): AdvertisementResponse {
        val user = loggedInUserService.getLoggedInUser() ?: throw ForbiddenException("User not found")
        val category = categoryRepository.findById(categoryId).orElseThrow { BadRequestException("Category not found") }

        val advertisementType = try {
            AdvertisementType.valueOf(type)
        } catch(e: IllegalArgumentException) {
            throw BadRequestException("Wrong advertisement type")
        }
        val advertisementStatus = if (advertisementType == AdvertisementType.FIXED_PRICE)
            AdvertisementStatus.AVAILABLE
        else
            AdvertisementStatus.BIDDING

        if (advertisementType == AdvertisementType.BID && (expiration == null || expiration.isBefore(OffsetDateTime.now()))) {
            throw BadRequestException("Expiration should be set for bids to a valid timestamp")
        }

        val advertisement = Advertisement(
            title,
            description,
            user.getId(),
            price,
            category,
            advertisementStatus,
            advertisementType,
            expiration)

        advertisementRepository.save(advertisement)

        if (!images.isNullOrEmpty()) {
            fileUploadService.uploadFiles(images, advertisement.id!!)
        }

        sendAdvertisementMessage("CREATE", advertisement.id!!, advertisement.title, advertisement.category.id)

        if (advertisement.type == AdvertisementType.BID) {
            try {
                bidApiClient.postCreate(CreateBidRequest()
                    .advertisementId(advertisement.id)
                    .userId(advertisement.advertiserId)
                    .price(advertisement.price)
                    .expiration(advertisement.expiration)
                    .title(advertisement.title))
            } catch (e: RestClientException) {
                sendAdvertisementMessage("DELETE", advertisement.id!!)
                fileUploadService.deleteImagesForAd(advertisement.id!!)
                throw ServiceUnavailableException("Bid service unavailable")
            }
        }

        return advertisementMapper.advertisementToAdvertisementResponse(advertisement)
    }

    fun deleteById(id: Int) {
        val user = loggedInUserService.getLoggedInUser() ?: throw ForbiddenException("User not found")
        val advertisement = advertisementRepository.findById(id)
            .orElseThrow { NotFoundException("Advertisement not found") }

        if (!loggedInUserService.isAdmin() && advertisement.advertiserId != user.getId()) {
            throw ForbiddenException("Can not delete advertisement")
        }

        fileUploadService.deleteImagesForAd(id)

        sendAdvertisementMessage("DELETE", id)

        if (advertisement.type == AdvertisementType.BID) {
            try {
                bidApiClient.deleteModify(advertisement.id)
            } catch (e: RestClientException) {
                throw ServiceUnavailableException("Bid service unavailable")
            }
        }

        advertisementRepository.delete(advertisement)
    }

    fun updateAdvertisement(
        id: Int,
        title: String,
        description: String,
        price: Double,
        categoryId: Int,
        status: String,
        images: MutableList<MultipartFile>?,
        deletedImages: String?
    ): AdvertisementResponse {
        val user = loggedInUserService.getLoggedInUser() ?: throw ForbiddenException("User not found")
        val advertisement = advertisementRepository.findByIdAndAdvertiserId(id, user.getId())
            .orElseThrow { ForbiddenException("Advertisement not found") }
        val category = categoryRepository.findById(categoryId)
            .orElseThrow { BadRequestException("Category not found") }

        if (advertisement.type == AdvertisementType.BID && advertisement.price != price) {
            throw BadRequestException("Bid start price cannot be changed")
        }

        advertisement.title = title
        advertisement.description = description
        advertisement.price = price
        advertisement.updatedAt = OffsetDateTime.now()
        advertisement.category = category
        if (advertisement.status.value != status) {
            val newStatus = AdvertisementStatus.valueOf(status)
            if (!validStateTransition(advertisement.status, newStatus)) {
                throw BadRequestException("Invalid status transition")
            }
            advertisement.status = newStatus
        }

        advertisementRepository.save(advertisement)

        if (!deletedImages.isNullOrEmpty()) {
            fileUploadService.deleteImagesByName(deletedImages.split(";"))
        }

        if (!images.isNullOrEmpty()) {
            fileUploadService.uploadFiles(images, advertisement.id!!)
        }

        sendAdvertisementMessage("UPDATE", id, advertisement.title, advertisement.category.id)

        if (advertisement.type == AdvertisementType.BID) {
            try {
                bidApiClient.putModify(advertisement.id,
                    ModifyBidRequest()
                        .title(advertisement.title)
                        .archived(advertisement.status == AdvertisementStatus.ARCHIVED))
            } catch (e: RestClientException) {
                throw ServiceUnavailableException("Bid service unavailable")
            }
        }

        return advertisementMapper.advertisementToAdvertisementResponse(advertisement)
    }

    private fun sendAdvertisementMessage(type: String, id: Int, title: String? = null, categoryId: Int? = null) {
        val mapper = ObjectMapper()
        val node = mapper.createObjectNode()
        node.put("type", type)
        node.put("advertisementId", id)
        if (title != null) node.put("title", title)
        if (categoryId != null) node.put("categoryId", categoryId)
        rabbitTemplate.convertAndSend(queue.name, node.toString())
    }

    private fun validStateTransition(from: AdvertisementStatus, to: AdvertisementStatus): Boolean {
        return when (from) {
            AdvertisementStatus.AVAILABLE -> to != AdvertisementStatus.BIDDING
            AdvertisementStatus.BIDDING -> listOf(AdvertisementStatus.SOLD, AdvertisementStatus.ARCHIVED).contains(to)
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
                categoryMapper.categoryToJavaCategoryResponse(it.key),
                it.value.map {
                    advertisementMapper.advertisementToAdvertisementResponse(it)
                })
        }
    }

    fun search(query: String): List<AdvertisementResponse> {
        val advertisements = advertisementRepository.findByTitleContainsOrDescriptionContains(query, query)
        return advertisements.map { advertisementMapper.advertisementToAdvertisementResponse(it) }
    }

    fun existsById(id: Int): AdvertisementExistsResponse {
        val advertisement = advertisementRepository.findById(id).orElseThrow { NotFoundException("Advertisement not found") }
        return advertisementMapper.advertisementToAdvertisementExistsResponse(advertisement)
    }

    fun getAdvertisementsByIds(ids: MutableList<Int>): List<AdvertisementDataResponse> {
        val advertisements = advertisementRepository.findAllById(ids)

        return advertisements.map { advertisementMapper.advertisementToAdvertisementDataResponse(it) }
    }
}
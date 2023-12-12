package hu.bme.aut.classifiedadvertisementsite.advertisementservice.unit

import hu.bme.aut.classifiedadvertisementsite.advertisementservice.client.java.api.model.CreateBidRequest
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.controller.exception.NotFoundException
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.Advertisement
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.AdvertisementStatus
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.AdvertisementType
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.Category
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.repository.AdvertisementRepository
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.repository.CategoryRepository
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.security.LoggedInUserService
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.security.UserDetailsImpl
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.service.AdvertisementService
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.service.FileUploadService
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.service.apiclient.BidApiClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.web.multipart.MultipartFile
import java.time.OffsetDateTime
import java.util.*
import org.mockito.Mockito.`when` as mockitoWhen

class AdvertisementServiceTest {
    private lateinit var advertisementRepository: AdvertisementRepository
    private lateinit var loggedInUserService: LoggedInUserService
    private lateinit var categoryRepository: CategoryRepository
    private lateinit var fileUploadService: FileUploadService
    private lateinit var rabbitTemplate: RabbitTemplate
    private lateinit var queue: Queue
    private lateinit var bidApiClient: BidApiClient

    private lateinit var advertisementService: AdvertisementService

    @BeforeEach
    fun init() {
        advertisementRepository = mock(AdvertisementRepository::class.java)
        loggedInUserService = mock(LoggedInUserService::class.java)
        categoryRepository = mock(CategoryRepository::class.java)
        fileUploadService = mock(FileUploadService::class.java)
        rabbitTemplate = mock(RabbitTemplate::class.java)
        queue = mock(Queue::class.java)
        bidApiClient = mock(BidApiClient::class.java)

        advertisementService = AdvertisementService(
            advertisementRepository,
            loggedInUserService,
            categoryRepository,
            fileUploadService,
            rabbitTemplate,
            queue,
            bidApiClient)
    }

    @Test
    fun `get advertisement by id`() {
        val id = 1
        val advertisement = Advertisement(
            "title",
            "description",
            1,
            5.2,
            Category("category", null, emptyList(), emptyList()),
            AdvertisementStatus.AVAILABLE,
            AdvertisementType.FIXED_PRICE,
            null,
            OffsetDateTime.now(),
            OffsetDateTime.now(),
            emptyList(),
            id)
        mockitoWhen(advertisementRepository.findById(id)).thenReturn(Optional.of(advertisement))

        val advertisementById = advertisementService.getAdvertisementById(id)

        verify(advertisementRepository).findById(id)
        assertEquals(id, advertisementById.id)
    }

    @Test
    fun `get not existing advertisement by id`() {
        val id = 1
        mockitoWhen(advertisementRepository.findById(1)).thenReturn(Optional.empty())

        assertThrows<NotFoundException> { advertisementService.getAdvertisementById(id) }

        verify(advertisementRepository).findById(id)
    }

    @Test
    fun `get advertisements by categoryId`() {
        val categoryId = 1
        val category = Category("category", null, emptyList(), emptyList(), categoryId)
        val advertisement = Advertisement(
            "title",
            "description",
            1,
            5.2,
            category,
            AdvertisementStatus.AVAILABLE,
            AdvertisementType.FIXED_PRICE,
            null,
            OffsetDateTime.now(),
            OffsetDateTime.now(),
            emptyList())
        mockitoWhen(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category))
        mockitoWhen(advertisementRepository.findByCategoryIn(anyList())).thenReturn(listOf(advertisement))

        val advertisementsByCategory = advertisementService.getAdvertisementsByCategory(categoryId)

        verify(categoryRepository).findById(categoryId)
        verify(advertisementRepository).findByCategoryIn(anyList())
        assertEquals(1, advertisementsByCategory.size)
    }

    @Test
    fun `get advertisements by categoryId category not exists`() {
        val categoryId = 1
        mockitoWhen(categoryRepository.findById(categoryId)).thenReturn(Optional.empty())

        assertThrows<NotFoundException> { advertisementService.getAdvertisementsByCategory(categoryId) }

        verify(categoryRepository).findById(categoryId)
        verify(advertisementRepository, never()).findByCategoryIn(anyList())
    }

    @Test
    fun `create advertisement`() {
        val userId = 1
        val categoryId = 2
        val advertisementId = 3
        val category = Category("category", null, emptyList(), emptyList(), categoryId)
        mockitoWhen(loggedInUserService.getLoggedInUser()).thenReturn(UserDetailsImpl(userId, "user", "user@test.local", true, listOf("ROLE_USER")))
        mockitoWhen(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category))
        mockitoWhen(advertisementRepository.save(any(Advertisement::class.java))).thenAnswer {
            it.getArgument<Advertisement>(0).id = advertisementId
            it.getArgument<Advertisement>(0)
        }

        val advertisement = advertisementService.createAdvertisement("title", "description", 2.6, categoryId, AdvertisementType.BID.value, OffsetDateTime.now().plusDays(1), mutableListOf(mock(MultipartFile::class.java)))

        verify(loggedInUserService).getLoggedInUser()
        verify(categoryRepository).findById(categoryId)
        verify(advertisementRepository).save(any(Advertisement::class.java))
        verify(fileUploadService).uploadFiles(anyList(), ArgumentMatchers.eq(advertisementId))
        verify(bidApiClient).postCreate(any(CreateBidRequest::class.java))
        verify(rabbitTemplate).convertAndSend(any(), anyString())
        assertEquals(advertisementId, advertisement.id)
    }

    @Test
    fun `delete advertisement`() {
        val userId = 1
        val advertisementId = 2
        val advertisement = Advertisement(
            "title",
            "description",
            1,
            5.2,
            Category("category", null, emptyList(), emptyList()),
            AdvertisementStatus.AVAILABLE,
            AdvertisementType.BID,
            OffsetDateTime.now().plusDays(1),
            OffsetDateTime.now(),
            OffsetDateTime.now(),
            emptyList(),
            advertisementId)
        mockitoWhen(advertisementRepository.findById(advertisementId)).thenReturn(Optional.of(advertisement))
        mockitoWhen(loggedInUserService.getLoggedInUser()).thenReturn(UserDetailsImpl(userId, "user", "user@test.local", true, listOf("ROLE_USER")))
        mockitoWhen(loggedInUserService.isAdmin()).thenReturn(false)

        advertisementService.deleteById(advertisementId)

        verify(loggedInUserService).getLoggedInUser()
        verify(loggedInUserService).isAdmin()
        verify(advertisementRepository).findById(advertisementId)
        verify(fileUploadService).deleteImagesForAd(advertisementId)
        verify(bidApiClient).deleteModify(advertisementId)
        verify(rabbitTemplate).convertAndSend(any(), anyString())
        verify(advertisementRepository).delete(any(Advertisement::class.java))
    }
}
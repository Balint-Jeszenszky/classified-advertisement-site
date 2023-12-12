package hu.bme.aut.classifiedadvertisementsite.advertisementservice.integration.internal

import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.*
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.repository.AdvertisementRepository
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.repository.CategoryRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.RequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AdvertisementInfoIT {
    @Autowired
    lateinit var mockMvc: MockMvc
    @Autowired
    lateinit var categoryRepository: CategoryRepository
    @Autowired
    lateinit var advertisementRepository: AdvertisementRepository
    val advertiserId = 1
    var advertisementId: Int? = null

    @BeforeEach
    fun init() {
        val category = categoryRepository.save(Category("category", null))
        val advertisement = advertisementRepository.save(
            Advertisement(
            "title",
            "description",
            advertiserId,
            5.5,
            category,
            AdvertisementStatus.AVAILABLE,
            AdvertisementType.FIXED_PRICE,
            null
        ))

        advertisementId = advertisement.id
    }

    @AfterEach
    fun cleanUp() {
        advertisementRepository.deleteAll()
        categoryRepository.deleteAll()
    }

    @Test
    fun `get advertisement info`() {
        val request: RequestBuilder = MockMvcRequestBuilders.get("/internal/advertisement/exists/{id}", advertisementId)
            .accept(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect((MockMvcResultMatchers.jsonPath("$.advertiserId").value(advertisementId)))
    }
}
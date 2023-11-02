package hu.bme.aut.classifiedadvertisementsite.bidservice.integration.internal

import hu.bme.aut.classifiedadvertisementsite.bidservice.api.internal.model.CreateBidRequest
import hu.bme.aut.classifiedadvertisementsite.bidservice.api.internal.model.ModifyBidRequest
import hu.bme.aut.classifiedadvertisementsite.bidservice.integration.util.JsonContentHelper.Companion.asJsonString
import hu.bme.aut.classifiedadvertisementsite.bidservice.model.Advertisement
import hu.bme.aut.classifiedadvertisementsite.bidservice.repository.AdvertisementRepository
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.OffsetDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class BidManagementControllerIT {
    @Autowired
    lateinit var mockMvc: MockMvc
    @Autowired
    lateinit var advertisementRepository: AdvertisementRepository

    @BeforeEach
    fun init() {
        val advertisement1 = Advertisement(1, 1, OffsetDateTime.now().plusDays(1), 10.0, "title1")
        advertisementRepository.save(advertisement1)
    }

    @AfterEach
    fun cleanUp() {
        advertisementRepository.deleteAll()
    }

    @Test
    fun `create advertisement`() {
        val requestBody = CreateBidRequest(2, "title2", 2, OffsetDateTime.now().plusDays(1), 25.0)

        val request: RequestBuilder = MockMvcRequestBuilders.post("/internal/create")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(asJsonString(requestBody))

        mockMvc.perform(request)
            .andExpect(status().isCreated)
    }

    @Test
    fun `modify advertisement`() {
        val requestBody = ModifyBidRequest(true, "title2")

        val request: RequestBuilder = MockMvcRequestBuilders.put("/internal/modify/1")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(asJsonString(requestBody))

        mockMvc.perform(request)
            .andExpect(status().isAccepted)
    }

    @Test
    fun `delete advertisement`() {

        val request: RequestBuilder = MockMvcRequestBuilders.delete("/internal/modify/1")
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andExpect(status().isNoContent)
    }
}
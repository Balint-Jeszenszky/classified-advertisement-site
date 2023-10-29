package hu.bme.aut.classifiedadvertisementsite.bidservice.integration.external

import hu.bme.aut.classifiedadvertisementsite.bidservice.model.Advertisement
import hu.bme.aut.classifiedadvertisementsite.bidservice.model.Bid
import hu.bme.aut.classifiedadvertisementsite.bidservice.repository.AdvertisementRepository
import hu.bme.aut.classifiedadvertisementsite.bidservice.repository.BidRepository
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.OffsetDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class BidListControllerIT {
    @Autowired
    lateinit var mockMvc: MockMvc
    @Autowired
    lateinit var advertisementRepository: AdvertisementRepository
    @Autowired
    lateinit var bidRepository: BidRepository

    @BeforeEach
    fun init() {
        val advertisement1 = Advertisement(1, 4, OffsetDateTime.now().plusDays(1), 10.0, "title1")
        val advertisement2 = Advertisement(2, 5, OffsetDateTime.now().plusDays(2), 20.0, "title2")

        advertisementRepository.saveAll(listOf(advertisement1, advertisement2))

        val bids = listOf(
            Bid(1, 25.0, advertisement1),
            Bid(2, 15.0, advertisement1),
            Bid(3, 35.0, advertisement1),
            Bid(2, 35.0, advertisement2),
            Bid(1, 45.0, advertisement2),
        )

        bidRepository.saveAll(bids)
    }

    @AfterEach
    fun cleanUp() {
        advertisementRepository.deleteAll()
    }

    @Test
    fun `get current bids by ids`() {
        val request: RequestBuilder = MockMvcRequestBuilders.get("/external/currentBids/1,2,3")
            .accept(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].price").value(35))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].price").value(45))
            .andExpect(jsonPath("$[2]").doesNotExist())
            .andExpect((jsonPath("$").isArray))
    }
}
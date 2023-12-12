package hu.bme.aut.classifiedadvertisementsite.advertisementservice.integration.external

import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.external.model.CommentRequest
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.integration.util.AuthHelper
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.integration.util.JsonContentHelper
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.*
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.repository.AdvertisementRepository
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.repository.CategoryRepository
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.repository.CommentRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
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
class CommentIT {
    @Autowired
    lateinit var mockMvc: MockMvc
    @Autowired
    lateinit var categoryRepository: CategoryRepository
    @Autowired
    lateinit var commentRepository: CommentRepository
    @Autowired
    lateinit var advertisementRepository: AdvertisementRepository
    val userId = 1
    var advertisementId: Int? = null
    var commentId: Int? = null

    @BeforeEach
    fun init() {
        val category = categoryRepository.save(Category("category", null))
        val advertisement = advertisementRepository.save(Advertisement(
            "title",
            "description",
            1,
            5.5,
            category,
            AdvertisementStatus.AVAILABLE,
            AdvertisementType.FIXED_PRICE,
            null
        ))

        advertisementId = advertisement.id

        val comment = commentRepository.save(Comment("comment", userId, advertisement, OffsetDateTime.now()))

        commentId = comment.id
    }

    @AfterEach
    fun cleanUp() {
        commentRepository.deleteAll()
        advertisementRepository.deleteAll()
        categoryRepository.deleteAll()
    }

    @Test
    fun `get comments by ad`() {
        val request: RequestBuilder = MockMvcRequestBuilders.get("/external/advertisement/{id}/comments", advertisementId)
            .accept(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andExpect(status().isOk)
            .andExpect((jsonPath("$").isArray))
    }

    @Test
    fun `create comment`() {
        val text = "text"
        val comment = CommentRequest(text)

        val request: RequestBuilder = MockMvcRequestBuilders.post("/external/advertisement/{id}/comments", advertisementId)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("x-user-data", AuthHelper.getUserAuthHeader(userId, "user", "user@user.localhost"))
            .content(JsonContentHelper.asJsonString(comment))

        mockMvc.perform(request)
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.content").value(text))
            .andExpect(jsonPath("$.creatorId").value(userId))
    }

    @Test
    fun `delete comment`() {
        val request: RequestBuilder = MockMvcRequestBuilders.delete("/external/comment/{id}", commentId)
            .header("x-user-data", AuthHelper.getAdminAuthHeader(userId, "user", "user@user.localhost"))

        mockMvc.perform(request)
            .andExpect(status().isNoContent)

        assertTrue(commentRepository.findById(commentId!!).isEmpty)
    }
}
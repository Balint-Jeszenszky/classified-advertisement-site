package hu.bme.aut.classifiedadvertisementsite.advertisementservice.integration.external

import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.external.model.CategoryRequest
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.integration.util.AuthHelper.Companion.getAdminAuthHeader
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.integration.util.AuthHelper.Companion.getUserAuthHeader
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.integration.util.JsonContentHelper.Companion.asJsonString
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.Category
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.repository.CategoryRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CategoryTest {
    @Autowired
    lateinit var mockMvc: MockMvc
    @Autowired
    lateinit var categoryRepository: CategoryRepository
    lateinit var mainCategory: Category;
    val MAIN_CATEGORY_NAME: String = "main"

    @BeforeEach
    fun init() {
        mainCategory = Category(MAIN_CATEGORY_NAME, null)
        categoryRepository.save(mainCategory)
    }

    @AfterEach
    fun cleanUp() {
        categoryRepository.deleteAll()
    }

    @Test
    fun `get categories`() {
        val request: RequestBuilder = MockMvcRequestBuilders.get("/external/categories")
            .accept(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(mainCategory.id))
            .andExpect(jsonPath("$[0].name").value(MAIN_CATEGORY_NAME))
            .andExpect(jsonPath("$[0].parentCategoryId").value(null))
            .andExpect((jsonPath("$").isArray))
    }

    @Test
    fun `create category`() {
        val subcategoryName = "subcategory"
        val subcategory = CategoryRequest(subcategoryName, mainCategory.id)

        val request: RequestBuilder = MockMvcRequestBuilders.post("/external/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("x-user-data", getAdminAuthHeader(1, "admin", "admin@admin.localhost"))
            .content(asJsonString(subcategory))

        mockMvc.perform(request)
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value(subcategoryName))
            .andExpect(jsonPath("$.parentCategoryId").value(mainCategory.id))
    }

    @Test
    fun `create category fails without admin role`() {
        val subcategoryName = "subcategory"
        val subcategory = CategoryRequest(subcategoryName, mainCategory.id)

        val request: RequestBuilder = MockMvcRequestBuilders.post("/external/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("x-user-data", getUserAuthHeader(2, "user", "user@user.localhost"))
            .content(asJsonString(subcategory))

        mockMvc.perform(request)
            .andExpect(status().isForbidden)
    }

    @Test
    fun `update category`() {
        val newCategoryName = "newCategoryName"
        val subcategory = CategoryRequest(newCategoryName, null)

        val request: RequestBuilder = MockMvcRequestBuilders.put("/external/categories/${mainCategory.id}")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("x-user-data", getAdminAuthHeader(1, "admin", "admin@admin.localhost"))
            .content(asJsonString(subcategory))

        mockMvc.perform(request)
            .andExpect(status().isAccepted)
            .andExpect(jsonPath("$.name").value(newCategoryName))
            .andExpect(jsonPath("$.parentCategoryId").value(null))

        assertEquals(categoryRepository.findById(mainCategory.id!!).get().name, newCategoryName)
    }

    @Test
    fun `update category fails without admin role`() {
        val newCategoryName = "newCategoryName"
        val subcategory = CategoryRequest(newCategoryName, mainCategory.id)

        val request: RequestBuilder = MockMvcRequestBuilders.put("/external/categories/${mainCategory.id}")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("x-user-data", getUserAuthHeader(2, "user", "user@user.localhost"))
            .content(asJsonString(subcategory))

        mockMvc.perform(request)
            .andExpect(status().isForbidden)
    }

    @Test
    fun `delete category`() {
        val request: RequestBuilder = MockMvcRequestBuilders.delete("/external/categories/${mainCategory.id}")
            .header("x-user-data", getAdminAuthHeader(1, "admin", "admin@admin.localhost"))

        mockMvc.perform(request)
            .andExpect(status().isNoContent)

        assertTrue(categoryRepository.findById(mainCategory.id!!).isEmpty)
    }

    @Test
    fun `delete category fails without admin role`() {
        val request: RequestBuilder = MockMvcRequestBuilders.delete("/external/categories/${mainCategory.id}")
            .header("x-user-data", getUserAuthHeader(2, "user", "user@user.localhost"))

        mockMvc.perform(request)
            .andExpect(status().isForbidden)

        assertTrue(categoryRepository.findById(mainCategory.id!!).isPresent)
    }
}
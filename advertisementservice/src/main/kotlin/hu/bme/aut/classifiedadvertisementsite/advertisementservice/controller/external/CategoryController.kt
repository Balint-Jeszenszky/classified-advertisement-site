package hu.bme.aut.classifiedadvertisementsite.advertisementservice.controller.external

import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.external.CategoryApi
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.external.model.CategoryRequest
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.external.model.CategoryResponse
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.controller.exception.BadRequestException
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.service.CategoryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class CategoryController(
    private val categoryService: CategoryService
) : ExternalApi, CategoryApi {

    @PreAuthorize("hasRole('ADMIN')")
    override fun deleteCategories(id: Int): ResponseEntity<Unit> {
        categoryService.deleteCategoryById(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    override fun getCategories(): ResponseEntity<List<CategoryResponse>> {
        val categories = categoryService.getCategories()
        return ResponseEntity(categories, HttpStatus.OK)
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun postCategories(categoryRequest: CategoryRequest?): ResponseEntity<CategoryResponse> {
        if (categoryRequest == null) {
            throw BadRequestException("Invalid data")
        }
        val category = categoryService.createCategory(categoryRequest)
        return ResponseEntity(category, HttpStatus.CREATED)
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun putCategories(id: Int, categoryRequest: CategoryRequest?): ResponseEntity<CategoryResponse> {
        if (categoryRequest == null) {
            throw BadRequestException("Invalid data")
        }
        val category = categoryService.modifyCategory(id, categoryRequest)
        return ResponseEntity(category, HttpStatus.ACCEPTED)
    }
}
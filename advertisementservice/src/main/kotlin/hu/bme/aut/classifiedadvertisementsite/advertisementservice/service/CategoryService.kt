package hu.bme.aut.classifiedadvertisementsite.advertisementservice.service

import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.external.model.CategoryRequest
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.external.model.CategoryResponse
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.controller.exception.BadRequestException
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.controller.exception.NotFoundException
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.mapper.CategoryMapper
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.Category
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.repository.CategoryRepository
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository
) {
    private val categoryMapper: CategoryMapper = Mappers.getMapper(CategoryMapper::class.java)

    fun deleteCategoryById(id: Int) {
        categoryRepository.deleteById(id)
    }

    fun getCategories(): List<CategoryResponse> {
        val categories = categoryRepository.findAll()

        return categories.map { categoryMapper.categoryToCategoryResponse(it) }
    }

    fun createCategory(categoryRequest: CategoryRequest): CategoryResponse {
        val parentCategory = if (categoryRequest.parentCategoryId == null) null
            else categoryRepository.findById(categoryRequest.parentCategoryId)
                .orElseThrow { BadRequestException("Parent category not exists") }
        val category = Category(
            categoryRequest.name,
            parentCategory
        )

        categoryRepository.save(category)

        return categoryMapper.categoryToCategoryResponse(category)
    }

    fun modifyCategory(id: Int, categoryRequest: CategoryRequest): CategoryResponse {
        val parentCategory = if (categoryRequest.parentCategoryId == null) null
            else categoryRepository.findById(categoryRequest.parentCategoryId)
                .orElseThrow { BadRequestException("Parent category not exists") }
        val category = categoryRepository.findById(id).orElseThrow { NotFoundException("Category not found") }

        category.name = categoryRequest.name
        category.parentCategory = parentCategory

        categoryRepository.save(category)

        return categoryMapper.categoryToCategoryResponse(category)
    }
}
package hu.bme.aut.classifiedadvertisementsite.advertisementservice.unit

import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.external.model.CategoryRequest
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.controller.exception.BadRequestException
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.Category
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.repository.CategoryRepository
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.service.CategoryService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.util.*
import org.mockito.Mockito.`when` as mockitoWhen

class CategoryServiceTest {
    private lateinit var categoryRepository: CategoryRepository
    private lateinit var categoryService: CategoryService

    @BeforeEach
    fun init() {
        categoryRepository = mock(CategoryRepository::class.java)
        categoryService = CategoryService(categoryRepository)
    }

    @Test
    fun `get categories`() {
        val category1 = Category("category1", null, mutableListOf(), mutableListOf(), 1)
        val category2 = Category("category2", category1, mutableListOf(), mutableListOf(), 2)
        mockitoWhen(categoryRepository.findAll()).thenReturn(listOf(category1, category2))

        val categories = categoryService.getCategories()

        assertEquals(2, categories.size)
        assertEquals(category1.name, categories.get(0).name)
        assertEquals(category1.id, categories.get(0).id)
        assertEquals(category1.parentCategory?.id, categories.get(0).parentCategoryId)
        assertEquals(category2.name, categories.get(1).name)
        assertEquals(category2.id, categories.get(1).id)
        assertEquals(category2.parentCategory?.id, categories.get(1).parentCategoryId)
    }

    @Test
    fun `create category without parent`() {
        val category = CategoryRequest("category", null)
        mockitoWhen(categoryRepository.save(any(Category::class.java))).thenReturn(
            Category(category.name, null, mutableListOf(), mutableListOf(), 1))

        val createdCategory = categoryService.createCategory(category)

        verify(categoryRepository, times(1)).save(any(Category::class.java))
        assertEquals(category.name, createdCategory.name)
        assertEquals(category.parentCategoryId, createdCategory.parentCategoryId)
    }

    @Test
    fun `create category with parent`() {
        val parentCategory = Category("parent", null, mutableListOf(), mutableListOf(), 1)
        val category = CategoryRequest("category", parentCategory.id)
        mockitoWhen(categoryRepository.findById(parentCategory.id!!)).thenReturn(Optional.of(parentCategory))
        mockitoWhen(categoryRepository.save(any(Category::class.java))).thenReturn(
            Category(category.name, parentCategory, mutableListOf(), mutableListOf()))

        val createdCategory = categoryService.createCategory(category)

        verify(categoryRepository, times(1)).findById(parentCategory.id!!)
        verify(categoryRepository, times(1)).save(any(Category::class.java))
        assertEquals(parentCategory.id, createdCategory.parentCategoryId)
        assertEquals(category.name, createdCategory.name)
    }

    @Test
    fun `create category fails with non existing parent`() {
        val parentCategoryId = 8
        val category = CategoryRequest("category", parentCategoryId)
        mockitoWhen(categoryRepository.findById(parentCategoryId!!)).thenReturn(Optional.empty())

        assertThrows<BadRequestException> { categoryService.createCategory(category) }

        verify(categoryRepository, times(1)).findById(parentCategoryId)
        verify(categoryRepository, times(0)).save(any(Category::class.java))
    }

    @Test
    fun `modify category`() {
        val category = CategoryRequest("category", null)
        val categoryId = 1
        mockitoWhen(categoryRepository.findById(categoryId)).thenReturn(
            Optional.of(Category("", Category("", null), mutableListOf(), mutableListOf(), categoryId)))
        mockitoWhen(categoryRepository.save(any(Category::class.java))).thenReturn(
            Category(category.name, null, mutableListOf(), mutableListOf(), categoryId))

        val createdCategory = categoryService.modifyCategory(categoryId, category)

        verify(categoryRepository, times(1)).findById(categoryId)
        verify(categoryRepository, times(1)).save(any(Category::class.java))
        assertEquals(category.name, createdCategory.name)
        assertEquals(category.parentCategoryId, createdCategory.parentCategoryId)
    }

    @Test
    fun `delete category`() {
        val categoryId = 1

        categoryService.deleteCategoryById(categoryId)

        verify(categoryRepository, times(1)).deleteById(categoryId)
    }
}
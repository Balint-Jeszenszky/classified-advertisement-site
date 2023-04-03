package hu.bme.aut.classifiedadvertisementsite.advertisementservice.mapper

import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.external.model.CategoryResponse
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.Category
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper
interface CategoryMapper {
    @Mapping(target = "parentCategoryId", source = "parentCategory.id")
    fun categoryToCategoryResponse(category: Category): CategoryResponse
}
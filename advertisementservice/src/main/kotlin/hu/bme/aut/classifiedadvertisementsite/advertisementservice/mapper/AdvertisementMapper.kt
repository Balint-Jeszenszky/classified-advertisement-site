package hu.bme.aut.classifiedadvertisementsite.advertisementservice.mapper

import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.external.model.AdvertisementResponse
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.Advertisement
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper
interface AdvertisementMapper {
    @Mapping(target = "categoryId", source = "category.id")
    fun advertisementToAdvertisementResponse(advertisement: Advertisement): AdvertisementResponse
}
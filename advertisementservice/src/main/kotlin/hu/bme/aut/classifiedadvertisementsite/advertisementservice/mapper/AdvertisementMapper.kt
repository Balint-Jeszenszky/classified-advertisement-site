package hu.bme.aut.classifiedadvertisementsite.advertisementservice.mapper

import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.external.model.AdvertisementResponse
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.Advertisement
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.AdvertisementStatus
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper
interface AdvertisementMapper {
    @Mapping(target = "categoryId", source = "category.id")
    fun advertisementToAdvertisementResponse(advertisement: Advertisement): AdvertisementResponse

    fun mapStatusEnum(advertisementStatus: AdvertisementStatus): AdvertisementResponse.Status {
//        return AdvertisementResponse.Status.valueOf(advertisementStatus.value) // IDK why it is not working
        return AdvertisementResponse.Status.values().first { it.value == advertisementStatus.value }
    }
}
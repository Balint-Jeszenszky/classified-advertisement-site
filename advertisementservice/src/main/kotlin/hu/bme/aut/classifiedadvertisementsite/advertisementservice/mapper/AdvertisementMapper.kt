package hu.bme.aut.classifiedadvertisementsite.advertisementservice.mapper

import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.internal.model.AdvertisementExistsResponse
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.java.api.external.model.AdvertisementDataResponse
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.java.api.external.model.AdvertisementResponse
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.Advertisement
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.AdvertisementStatus
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper
interface AdvertisementMapper {
    @Mapping(target = "categoryId", source = "category.id")
    fun advertisementToAdvertisementResponse(advertisement: Advertisement): AdvertisementResponse

    fun advertisementToAdvertisementExistsResponse(advertisement: Advertisement): AdvertisementExistsResponse

    fun advertisementToAdvertisementDataResponse(advertisement: Advertisement): AdvertisementDataResponse

    fun mapStatusEnum(advertisementStatus: AdvertisementStatus): AdvertisementResponse.StatusEnum {
//        return AdvertisementResponse.Status.valueOf(advertisementStatus.value) // IDK why it is not working
        return AdvertisementResponse.StatusEnum.values().first { it.value == advertisementStatus.value }
    }
}
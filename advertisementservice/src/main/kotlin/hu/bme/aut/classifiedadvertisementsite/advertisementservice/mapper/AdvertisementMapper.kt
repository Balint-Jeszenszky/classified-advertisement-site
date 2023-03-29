package hu.bme.aut.classifiedadvertisementsite.advertisementservice.mapper

import hu.bme.aut.classifiedadvertisementsite.advertisementservice.api.external.model.AdvertisementResponse
import hu.bme.aut.classifiedadvertisementsite.advertisementservice.model.Advertisement
import org.mapstruct.Mapper

@Mapper
interface AdvertisementMapper {
    fun advertisementToAdvertisementResponse(advertisement: Advertisement): AdvertisementResponse
}
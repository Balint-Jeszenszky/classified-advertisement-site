package hu.bme.aut.classifiedadvertisementsite.bidservice.mapper

import hu.bme.aut.classifiedadvertisementsite.bidservice.api.external.model.BidResponse
import hu.bme.aut.classifiedadvertisementsite.bidservice.model.Bid
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper
interface BidMapper {
    @Mapping(target = "id", source = "advertisement.id")
    fun bidToBidResponse(bid: Bid): BidResponse
}
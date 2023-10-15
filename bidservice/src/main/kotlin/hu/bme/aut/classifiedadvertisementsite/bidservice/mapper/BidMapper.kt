package hu.bme.aut.classifiedadvertisementsite.bidservice.mapper

import hu.bme.aut.classifiedadvertisementsite.bidservice.api.external.model.BidResponse
import hu.bme.aut.classifiedadvertisementsite.bidservice.model.Bid
import org.mapstruct.Mapper

@Mapper
interface BidMapper {
    fun bidToBidResponse(bid: Bid): BidResponse
}
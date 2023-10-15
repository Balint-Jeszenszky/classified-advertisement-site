package hu.bme.aut.classifiedadvertisementsite.bidservice.service

import hu.bme.aut.classifiedadvertisementsite.bidservice.api.external.model.BidResponse
import hu.bme.aut.classifiedadvertisementsite.bidservice.mapper.BidMapper
import hu.bme.aut.classifiedadvertisementsite.bidservice.repository.AdvertisementRepository
import hu.bme.aut.classifiedadvertisementsite.bidservice.repository.BidRepository
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service

@Service
class BidService(
    private val advertisementRepository: AdvertisementRepository,
    private val bidRepository: BidRepository,
) {
    private val bidMapper: BidMapper = Mappers.getMapper(BidMapper::class.java)

    fun findBidsByAdvertisementIds(ids: List<Int>): List<BidResponse> {
        val bids = bidRepository.findTopBidsForAdvertisementsByIds(ids)

        return bids.map { bidMapper.bidToBidResponse(it) }
    }
}
package hu.bme.aut.classifiedadvertisementsite.bidservice.controller.external

import hu.bme.aut.classifiedadvertisementsite.bidservice.api.external.BidApi
import hu.bme.aut.classifiedadvertisementsite.bidservice.api.external.model.BidResponse
import hu.bme.aut.classifiedadvertisementsite.bidservice.service.BidService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class BidListController(
    private val bidService: BidService,
) : ExternalApi, BidApi {
    override fun getCurrentBidsIds(ids: List<Int>): ResponseEntity<List<BidResponse>> {
        val bids = bidService.findBidsByAdvertisementIds(ids)

        return ResponseEntity(bids, HttpStatus.OK)
    }
}
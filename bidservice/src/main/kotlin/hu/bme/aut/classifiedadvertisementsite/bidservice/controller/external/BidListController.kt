package hu.bme.aut.classifiedadvertisementsite.bidservice.controller.external

import hu.bme.aut.classifiedadvertisementsite.bidservice.api.external.BidApi
import hu.bme.aut.classifiedadvertisementsite.bidservice.api.external.model.BidResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class BidListController : BidApi, ExternalApi {
    override fun getCurrentBidsIds(ids: List<Int>): ResponseEntity<List<BidResponse>> {
        TODO("Not yet implemented")
    }
}
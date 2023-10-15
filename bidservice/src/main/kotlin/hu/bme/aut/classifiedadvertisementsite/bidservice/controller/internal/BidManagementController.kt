package hu.bme.aut.classifiedadvertisementsite.bidservice.controller.internal

import hu.bme.aut.classifiedadvertisementsite.bidservice.api.internal.BidApi
import hu.bme.aut.classifiedadvertisementsite.bidservice.api.internal.model.CreateBidRequest
import hu.bme.aut.classifiedadvertisementsite.bidservice.api.internal.model.ModifyBidRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class BidManagementController : BidApi, InternalApi {
    override fun deleteModify(id: Int): ResponseEntity<Unit> {
        TODO("Not yet implemented")
    }

    override fun postCreate(createBidRequest: CreateBidRequest?): ResponseEntity<Unit> {
        TODO("Not yet implemented")
    }

    override fun putModify(id: Int, modifyBidRequest: ModifyBidRequest?): ResponseEntity<Unit> {
        TODO("Not yet implemented")
    }
}
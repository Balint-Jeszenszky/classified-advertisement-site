package hu.bme.aut.classifiedadvertisementsite.bidservice.controller.internal

import hu.bme.aut.classifiedadvertisementsite.bidservice.api.internal.BidApi
import hu.bme.aut.classifiedadvertisementsite.bidservice.api.internal.model.CreateBidRequest
import hu.bme.aut.classifiedadvertisementsite.bidservice.api.internal.model.ModifyBidRequest
import hu.bme.aut.classifiedadvertisementsite.bidservice.controller.exception.BadRequestException
import hu.bme.aut.classifiedadvertisementsite.bidservice.service.BidService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class BidManagementController(
    private val bidService: BidService,
) : InternalApi, BidApi {
    override fun deleteModify(id: Int): ResponseEntity<Unit> {
        bidService.deleteAdvertisement(id)

        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    override fun postCreate(createBidRequest: CreateBidRequest?): ResponseEntity<Unit> {
        checkRequestBody(createBidRequest == null)
        bidService.createAdvertisement(createBidRequest!!)

        return ResponseEntity(HttpStatus.CREATED)
    }

    override fun putModify(id: Int, modifyBidRequest: ModifyBidRequest?): ResponseEntity<Unit> {
        checkRequestBody(modifyBidRequest == null)
        bidService.modifyAdvertisement(id, modifyBidRequest!!)

        return ResponseEntity(HttpStatus.ACCEPTED)
    }

    private fun checkRequestBody(body: Any?) {
        if (body == null) {
            throw BadRequestException("Request body missing")
        }
    }
}
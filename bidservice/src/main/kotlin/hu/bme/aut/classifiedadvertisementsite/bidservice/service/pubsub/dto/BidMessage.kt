package hu.bme.aut.classifiedadvertisementsite.bidservice.service.pubsub.dto

import java.io.Serializable

data class BidMessage(
    val advertisementId: Int,
    val price: Double,
) : Serializable

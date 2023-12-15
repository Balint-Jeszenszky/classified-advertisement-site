package hu.bme.aut.classifiedadvertisementsite.bidservice.service.pubsub

import hu.bme.aut.classifiedadvertisementsite.bidservice.service.pubsub.dto.BidMessage

interface MessagePublisher {
    fun publish(message: BidMessage)
}
package hu.bme.aut.classifiedadvertisementsite.bidservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.aut.classifiedadvertisementsite.bidservice.api.external.model.BidResponse
import hu.bme.aut.classifiedadvertisementsite.bidservice.mapper.BidMapper
import hu.bme.aut.classifiedadvertisementsite.bidservice.model.Bid
import hu.bme.aut.classifiedadvertisementsite.bidservice.repository.AdvertisementRepository
import hu.bme.aut.classifiedadvertisementsite.bidservice.repository.BidRepository
import jakarta.transaction.Transactional
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession


@Service
class BidService(
    private val advertisementRepository: AdvertisementRepository,
    private val bidRepository: BidRepository,
) {
    private val bidMapper: BidMapper = Mappers.getMapper(BidMapper::class.java)
    private val subscriptions: MutableMap<Int, MutableList<WebSocketSession>> = mutableMapOf()

    companion object {
        private const val SUBSCRIBED_ADVERTISEMENT_ID = "advertisementId"
    }

    fun findBidsByAdvertisementIds(ids: List<Int>): List<BidResponse> {
        val bids = bidRepository.findTopBidsForAdvertisementsByIds(ids)

        return bids.map { bidMapper.bidToBidResponse(it) }
    }

    fun subscribeForBids(session: WebSocketSession, advertisementId: Int) {
        if (advertisementRepository.existsById(advertisementId).not()) {
            return
        }

        bidRepository.findFirstByAdvertisement_IdOrderByPriceDesc(advertisementId).ifPresent {
            session.sendMessage(createPriceMessage(it.price))
        }

        if (subscriptions.containsKey(advertisementId).not()) {
            subscriptions[advertisementId] = mutableListOf()
        }

        subscriptions[advertisementId]?.add(session)

        if (session.attributes.containsKey(SUBSCRIBED_ADVERTISEMENT_ID)) {
            subscriptions[session.attributes[SUBSCRIBED_ADVERTISEMENT_ID]]?.remove(session)
        }

        session.attributes[SUBSCRIBED_ADVERTISEMENT_ID] = advertisementId
    }

    @Transactional
    fun createBid(userId: Int, advertisementId: Int, price: Double) {
        val advertisement = advertisementRepository.findById(advertisementId)

        if (advertisement.isEmpty) {
            return
        }

        val lastBid = bidRepository.findFirstByAdvertisement_IdOrderByPriceDesc(advertisementId)

        if (lastBid.isPresent && lastBid.get().price >= price) {
            return
        }

        bidRepository.save(Bid(userId, price, advertisement.get()))

        subscriptions[advertisementId]?.forEach {
            if (it.isOpen) it.sendMessage(createPriceMessage(price))
        }
    }

    fun removeSession(session: WebSocketSession) {
        if (session.attributes.containsKey(SUBSCRIBED_ADVERTISEMENT_ID).not()) {
            return
        }
        subscriptions[session.attributes[SUBSCRIBED_ADVERTISEMENT_ID]]?.remove(session)
    }

    private fun createPriceMessage(price: Double): TextMessage {
        val mapper = ObjectMapper()
        val node = mapper.createObjectNode()
        node.put("price", price)
        return TextMessage(node.toString())
    }
}
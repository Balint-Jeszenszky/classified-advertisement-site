package hu.bme.aut.classifiedadvertisementsite.bidservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.aut.classifiedadvertisementsite.bidservice.api.external.model.BidResponse
import hu.bme.aut.classifiedadvertisementsite.bidservice.api.internal.model.CreateBidRequest
import hu.bme.aut.classifiedadvertisementsite.bidservice.api.internal.model.ModifyBidRequest
import hu.bme.aut.classifiedadvertisementsite.bidservice.controller.exception.NotFoundException
import hu.bme.aut.classifiedadvertisementsite.bidservice.mapper.BidMapper
import hu.bme.aut.classifiedadvertisementsite.bidservice.model.Advertisement
import hu.bme.aut.classifiedadvertisementsite.bidservice.model.Bid
import hu.bme.aut.classifiedadvertisementsite.bidservice.repository.AdvertisementRepository
import hu.bme.aut.classifiedadvertisementsite.bidservice.repository.BidRepository
import hu.bme.aut.classifiedadvertisementsite.bidservice.service.pubsub.RedisMessagePublisher
import hu.bme.aut.classifiedadvertisementsite.bidservice.service.pubsub.dto.BidMessage
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.time.OffsetDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList


@Service
class BidService(
    private val advertisementRepository: AdvertisementRepository,
    private val bidRepository: BidRepository,
    private val redisMessagePublisher: RedisMessagePublisher,
) {
    private val bidMapper: BidMapper = Mappers.getMapper(BidMapper::class.java)
    private val subscriptions: ConcurrentHashMap<Int, CopyOnWriteArrayList<WebSocketSession>> = ConcurrentHashMap()

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
            subscriptions[advertisementId] = CopyOnWriteArrayList()
        }

        subscriptions[advertisementId]?.add(session)

        if (session.attributes.containsKey(SUBSCRIBED_ADVERTISEMENT_ID)) {
            subscriptions[session.attributes[SUBSCRIBED_ADVERTISEMENT_ID]]?.remove(session)
        }

        session.attributes[SUBSCRIBED_ADVERTISEMENT_ID] = advertisementId
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    fun createBid(userId: Int, advertisementId: Int, price: Double) {
        val advertisement = advertisementRepository.findById(advertisementId)

        if (advertisement.isEmpty
            || advertisement.get().archived
            || advertisement.get().expiration.isBefore(OffsetDateTime.now())) {
            return
        }

        val lastBid = bidRepository.findFirstByAdvertisement_IdOrderByPriceDesc(advertisementId)

        if (lastBid.isPresent && lastBid.get().price >= price) {
            return
        }

        bidRepository.save(Bid(userId, price, advertisement.get()))

        redisMessagePublisher.publish(BidMessage(advertisementId, price))
    }

    fun notifyBid(bidMessage: BidMessage) {
        subscriptions[bidMessage.advertisementId]?.forEach {
            if (it.isOpen) it.sendMessage(createPriceMessage(bidMessage.price))
        }
    }

    fun removeSession(session: WebSocketSession) {
        if (session.attributes.containsKey(SUBSCRIBED_ADVERTISEMENT_ID).not()) {
            return
        }
        subscriptions[session.attributes[SUBSCRIBED_ADVERTISEMENT_ID]]?.remove(session)
    }

    fun createAdvertisement(createBidRequest: CreateBidRequest) {
        val advertisement = Advertisement(
            createBidRequest.advertisementId,
            createBidRequest.userId,
            createBidRequest.expiration,
            createBidRequest.price,
            createBidRequest.title)

        advertisementRepository.save(advertisement)
    }

    fun modifyAdvertisement(id: Int, modifyBidRequest: ModifyBidRequest) {
        val advertisement = advertisementRepository.findById(id)

        if (advertisement.isEmpty) {
            throw NotFoundException("Advertisement not found")
        }

        val storedAdvertisement = advertisement.get()
        storedAdvertisement.archived = modifyBidRequest.archived
        storedAdvertisement.title = modifyBidRequest.title

        advertisementRepository.save(storedAdvertisement)

        subscriptions.remove(id)
    }

    fun deleteAdvertisement(id: Int) {
        advertisementRepository.deleteById(id)

        subscriptions.remove(id)
    }

    private fun createPriceMessage(price: Double): TextMessage {
        val mapper = ObjectMapper()
        val node = mapper.createObjectNode()
        node.put("price", price)
        return TextMessage(node.toString())
    }
}
package hu.bme.aut.classifiedadvertisementsite.bidservice.unit

import hu.bme.aut.classifiedadvertisementsite.bidservice.api.internal.model.ModifyBidRequest
import hu.bme.aut.classifiedadvertisementsite.bidservice.controller.exception.NotFoundException
import hu.bme.aut.classifiedadvertisementsite.bidservice.model.Advertisement
import hu.bme.aut.classifiedadvertisementsite.bidservice.model.Bid
import hu.bme.aut.classifiedadvertisementsite.bidservice.repository.AdvertisementRepository
import hu.bme.aut.classifiedadvertisementsite.bidservice.repository.BidRepository
import hu.bme.aut.classifiedadvertisementsite.bidservice.service.BidService
import hu.bme.aut.classifiedadvertisementsite.bidservice.service.pubsub.RedisMessagePublisher
import hu.bme.aut.classifiedadvertisementsite.bidservice.service.pubsub.dto.BidMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.time.OffsetDateTime
import java.util.*


class BidServiceTest {
    private lateinit var advertisementRepository: AdvertisementRepository
    private lateinit var bidRepository: BidRepository
    private lateinit var redisMessagePublisher: RedisMessagePublisher

    private lateinit var bidService: BidService

    @BeforeEach
    fun init() {
        advertisementRepository = mock(AdvertisementRepository::class.java)
        bidRepository = mock(BidRepository::class.java)
        redisMessagePublisher = mock(RedisMessagePublisher::class.java)

        bidService = BidService(
            advertisementRepository,
            bidRepository,
            redisMessagePublisher,
        )
    }

    @Test
    fun `find bids by advertisement ids`() {
        val ids = listOf(1, 2, 3)
        val bids = listOf(
            Bid(1, 25.0, Advertisement(1, 2, OffsetDateTime.now(), 10.0, "title1"), 1),
            Bid(1, 25.0, Advertisement(2, 2, OffsetDateTime.now(), 10.0, "title2"), 2),
            Bid(1, 25.0, Advertisement(3, 2, OffsetDateTime.now(), 10.0, "title3"), 3),
        )
        `when`(bidRepository.findTopBidsForAdvertisementsByIds(ids)).thenReturn(bids)

        val bidResponses = bidService.findBidsByAdvertisementIds(ids)

        assertEquals(bids.size, bidResponses.size)
    }

    @Test
    fun `subscribe for bids advertisement not exists`() {
        val id = 5
        `when`(advertisementRepository.existsById(id)).thenReturn(false)
        val session = mock(WebSocketSession::class.java)

        bidService.subscribeForBids(session, id)

        verify(bidRepository, never()).findFirstByAdvertisement_IdOrderByPriceDesc(id)
    }

    @Test
    fun `subscribe for bids advertisement exists`() {
        val id = 5
        `when`(advertisementRepository.existsById(id)).thenReturn(true)
        `when`(bidRepository.findFirstByAdvertisement_IdOrderByPriceDesc(id)).thenReturn(
            Optional.of(Bid(1, 25.0, Advertisement(1, 2, OffsetDateTime.now(), 10.0, "title"), 1)))
        val session = mock(WebSocketSession::class.java)

        bidService.subscribeForBids(session, id)

        verify(bidRepository).findFirstByAdvertisement_IdOrderByPriceDesc(id)
        verify(session).sendMessage(any(TextMessage::class.java))
        verify(session, times(2)).attributes
    }

    @Test
    fun `create bid advertisement not exists`() {
        val advertisementId = 5
        val userId = 1
        val price = 25.0
        `when`(advertisementRepository.findById(advertisementId)).thenReturn(Optional.empty())

        bidService.createBid(userId, advertisementId, price)

        verify(advertisementRepository).findById(advertisementId)
        verify(bidRepository, never()).findFirstByAdvertisement_IdOrderByPriceDesc(advertisementId)
        verify(bidRepository, never()).save(any(Bid::class.java))
        verify(redisMessagePublisher, never()).publish(BidMessage(advertisementId, price, userId))
    }

    @Test
    fun `create bid advertisement archived`() {
        val advertisementId = 5
        val userId = 1
        val price = 25.0
        `when`(advertisementRepository.findById(advertisementId)).thenReturn(
            Optional.of(Advertisement(advertisementId, 2, OffsetDateTime.now().plusDays(1), 10.0, "title", true)))

        bidService.createBid(userId, advertisementId, price)

        verify(advertisementRepository).findById(advertisementId)
        verify(bidRepository, never()).findFirstByAdvertisement_IdOrderByPriceDesc(advertisementId)
        verify(bidRepository, never()).save(any(Bid::class.java))
        verify(redisMessagePublisher, never()).publish(BidMessage(advertisementId, price, userId))
    }

    @Test
    fun `create bid advertisement expired`() {
        val advertisementId = 5
        val userId = 1
        val price = 25.0
        `when`(advertisementRepository.findById(advertisementId)).thenReturn(
            Optional.of(Advertisement(advertisementId, 2, OffsetDateTime.now().minusDays(1), 10.0, "title" )))

        bidService.createBid(userId, advertisementId, price)

        verify(advertisementRepository).findById(advertisementId)
        verify(bidRepository, never()).findFirstByAdvertisement_IdOrderByPriceDesc(advertisementId)
        verify(bidRepository, never()).save(any(Bid::class.java))
        verify(redisMessagePublisher, never()).publish(BidMessage(advertisementId, price, userId))
    }

    @Test
    fun `create bid less than last bid`() {
        val advertisementId = 5
        val userId = 1
        val price = 15.0
        val advertisement = Advertisement(advertisementId, 2, OffsetDateTime.now().plusDays(1), 10.0, "title" )
        `when`(advertisementRepository.findById(advertisementId)).thenReturn(
            Optional.of(advertisement))
        `when`(bidRepository.findFirstByAdvertisement_IdOrderByPriceDesc(advertisementId)).thenReturn(
            Optional.of(Bid(3, 20.0, advertisement))
        )

        bidService.createBid(userId, advertisementId, price)

        verify(advertisementRepository).findById(advertisementId)
        verify(bidRepository).findFirstByAdvertisement_IdOrderByPriceDesc(advertisementId)
        verify(bidRepository, never()).save(any(Bid::class.java))
        verify(redisMessagePublisher, never()).publish(BidMessage(advertisementId, price, userId))
    }

    @Test
    fun `create bid equal to last bid`() {
        val advertisementId = 5
        val userId = 1
        val price = 15.0
        val advertisement = Advertisement(advertisementId, 2, OffsetDateTime.now().plusDays(1), 10.0, "title" )
        `when`(advertisementRepository.findById(advertisementId)).thenReturn(
            Optional.of(advertisement))
        `when`(bidRepository.findFirstByAdvertisement_IdOrderByPriceDesc(advertisementId)).thenReturn(
            Optional.of(Bid(3, price, advertisement))
        )

        bidService.createBid(userId, advertisementId, price)

        verify(advertisementRepository).findById(advertisementId)
        verify(bidRepository).findFirstByAdvertisement_IdOrderByPriceDesc(advertisementId)
        verify(bidRepository, never()).save(any(Bid::class.java))
        verify(redisMessagePublisher, never()).publish(BidMessage(advertisementId, price, userId))
    }

    @Test
    fun `create bid more than last`() {
        val advertisementId = 5
        val userId = 1
        val price = 25.0
        val advertisement = Advertisement(advertisementId, 2, OffsetDateTime.now().plusDays(1), 10.0, "title" )
        `when`(advertisementRepository.findById(advertisementId)).thenReturn(
            Optional.of(advertisement))
        `when`(bidRepository.findFirstByAdvertisement_IdOrderByPriceDesc(advertisementId)).thenReturn(
            Optional.of(Bid(3, 15.0, advertisement))
        )

        bidService.createBid(userId, advertisementId, price)

        verify(advertisementRepository).findById(advertisementId)
        verify(bidRepository).findFirstByAdvertisement_IdOrderByPriceDesc(advertisementId)
        verify(bidRepository).save(argThat { it.price == price && it.userId == userId && it.advertisement == advertisement })
        verify(redisMessagePublisher).publish(BidMessage(advertisementId, price, userId))
    }

    @Test
    fun `modify advertisement not exists`() {
        val id = 1
        `when`(advertisementRepository.findById(id)).thenReturn(Optional.empty())

        assertThrows<NotFoundException> { bidService.modifyAdvertisement(id, ModifyBidRequest(true, "title")) }

        verify(advertisementRepository, never()).save(any(Advertisement::class.java))
    }
}
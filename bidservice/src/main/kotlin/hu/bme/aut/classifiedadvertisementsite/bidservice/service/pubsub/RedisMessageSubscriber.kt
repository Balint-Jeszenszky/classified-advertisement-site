package hu.bme.aut.classifiedadvertisementsite.bidservice.service.pubsub

import hu.bme.aut.classifiedadvertisementsite.bidservice.service.BidService
import hu.bme.aut.classifiedadvertisementsite.bidservice.service.pubsub.dto.BidMessage
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Service

@Service
class RedisMessageSubscriber(
    private val bidService: BidService,
) : MessageListener {

    override fun onMessage(message: Message, pattern: ByteArray?) {
        val bidMessage = Util.deserialize<BidMessage>(message.body)

        if (bidMessage == null) {
            return
        }

        bidService.notifyBid(bidMessage)
    }
}
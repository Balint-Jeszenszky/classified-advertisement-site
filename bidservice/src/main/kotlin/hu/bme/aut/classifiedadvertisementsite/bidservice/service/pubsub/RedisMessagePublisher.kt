package hu.bme.aut.classifiedadvertisementsite.bidservice.service.pubsub

import hu.bme.aut.classifiedadvertisementsite.bidservice.service.pubsub.dto.BidMessage
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.stereotype.Service

@Service
class RedisMessagePublisher(
    private var redisTemplate: RedisTemplate<String, Any>,
    private var topic: ChannelTopic,
) : MessagePublisher {
    override fun publish(message: BidMessage) {
        redisTemplate.convertAndSend(topic.topic, message)
    }
}
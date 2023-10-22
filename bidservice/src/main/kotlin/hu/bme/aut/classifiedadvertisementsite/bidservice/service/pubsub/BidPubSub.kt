package hu.bme.aut.classifiedadvertisementsite.bidservice.service.pubsub

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter
import org.springframework.stereotype.Component

@Component
class BidPubSub(
    @Value("\${redis.host}") private val host: String,
    @Value("\${redis.port}") private val port: Int,
) {

    @Bean
    fun jedisConnectionFactory(): JedisConnectionFactory {
        return JedisConnectionFactory(RedisStandaloneConfiguration(host, port))
    }

    @Bean
    fun redisTemplate(jedisConnectionFactory: JedisConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = jedisConnectionFactory
        return template
    }

    @Bean
    fun messageListener(redisMessageSubscriber: RedisMessageSubscriber): MessageListenerAdapter {
        return MessageListenerAdapter(redisMessageSubscriber)
    }

    @Bean
    fun redisContainer(
        jedisConnectionFactory: JedisConnectionFactory,
        messageListener: MessageListenerAdapter,
        topic: ChannelTopic
    ): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(jedisConnectionFactory)
        container.addMessageListener(messageListener, topic)
        return container
    }

    @Bean
    fun redisPublisher(redisTemplate: RedisTemplate<String, Any>, topic: ChannelTopic): MessagePublisher {
        return RedisMessagePublisher(redisTemplate, topic)
    }

    @Bean
    fun topic(): ChannelTopic {
        return ChannelTopic("newBid")
    }
}
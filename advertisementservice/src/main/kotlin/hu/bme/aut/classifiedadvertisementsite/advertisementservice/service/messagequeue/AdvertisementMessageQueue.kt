package hu.bme.aut.classifiedadvertisementsite.advertisementservice.service.messagequeue

import org.springframework.amqp.core.Queue
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class AdvertisementMessageQueue {
    private val QUEUE_NAME = "advertisement-queue"

    @Bean("advertisement-queue")
    fun advertisementQueue(): Queue {
        return Queue(QUEUE_NAME, true)
    }
}
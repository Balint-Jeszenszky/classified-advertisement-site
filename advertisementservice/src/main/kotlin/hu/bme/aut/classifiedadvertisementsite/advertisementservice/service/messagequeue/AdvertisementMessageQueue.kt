package hu.bme.aut.classifiedadvertisementsite.advertisementservice.service.messagequeue

import org.springframework.amqp.core.Queue
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class AdvertisementMessageQueue {
    companion object {
        const val QUEUE_NAME = "advertisement-queue"
    }

    @Bean(QUEUE_NAME)
    fun advertisementQueue(): Queue {
        return Queue(QUEUE_NAME, true)
    }
}
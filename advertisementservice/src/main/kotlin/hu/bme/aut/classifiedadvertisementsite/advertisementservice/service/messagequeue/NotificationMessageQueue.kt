package hu.bme.aut.classifiedadvertisementsite.advertisementservice.service.messagequeue

import org.springframework.amqp.core.Queue
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class NotificationMessageQueue {
    private val QUEUE_NAME = "image-processing-queue"

    @Bean
    fun queue(): Queue {
        return Queue(QUEUE_NAME, true)
    }
}
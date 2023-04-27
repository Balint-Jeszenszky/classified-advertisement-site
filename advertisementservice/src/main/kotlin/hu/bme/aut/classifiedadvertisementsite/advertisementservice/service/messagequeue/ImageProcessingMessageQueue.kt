package hu.bme.aut.classifiedadvertisementsite.advertisementservice.service.messagequeue

import org.springframework.amqp.core.Queue
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class ImageProcessingMessageQueue {
    private val QUEUE_NAME = "image-processing-queue"

    @Bean("image-processing-queue")
    fun imageProcessingQueue(): Queue {
        return Queue(QUEUE_NAME, true)
    }
}
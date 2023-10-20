package hu.bme.aut.classifiedadvertisementsite.bidservice.service.messagequeue

import org.springframework.amqp.core.Queue
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class NotificationMessageQueue {
    companion object {
        const val EMAIL_QUEUE_NAME = "email-queue"
        const val PUSH_QUEUE_NAME = "push-queue"
    }

    @Bean("email-queue")
    fun emailQueue(): Queue {
        return Queue(EMAIL_QUEUE_NAME, true)
    }

    @Bean("push-queue")
    fun pushQueue(): Queue {
        return Queue(PUSH_QUEUE_NAME, true)
    }
}
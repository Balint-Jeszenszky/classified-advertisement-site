package hu.bme.aut.classifiedadvertisementsite.bidservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import hu.bme.aut.classifiedadvertisementsite.bidservice.client.java.api.model.UserDataResponse
import hu.bme.aut.classifiedadvertisementsite.bidservice.model.Advertisement
import hu.bme.aut.classifiedadvertisementsite.bidservice.model.Bid
import hu.bme.aut.classifiedadvertisementsite.bidservice.repository.AdvertisementRepository
import hu.bme.aut.classifiedadvertisementsite.bidservice.repository.BidRepository
import hu.bme.aut.classifiedadvertisementsite.bidservice.service.apiclient.UserApiClient
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import java.time.OffsetDateTime


@Service
class NotificationService(
    private val advertisementRepository: AdvertisementRepository,
    private val bidRepository: BidRepository,
    private val rabbitTemplate: RabbitTemplate,
    @Qualifier("email-queue") private val emailQueue: Queue,
    @Qualifier("push-queue") private val pushQueue: Queue,
    private val userApiClient: UserApiClient
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "0 */5 * * * *")
    @SchedulerLock(name = "notifyBidWinners", lockAtLeastFor = "PT3M", lockAtMostFor = "PT4M")
    protected fun notifyBidWinners() {
        log.info("notifying winners started")
        val advertisements = advertisementRepository
            .findAllByExpirationBeforeAndArchivedIsFalseAndNotifiedIsFalse(OffsetDateTime.now())
        val bids = bidRepository.findTopBidsForAdvertisementsByIds(advertisements.map { it.id })

        val users = try {
            if (bids.isNotEmpty()) userApiClient.getUserDetailsIds(bids.map { it.userId }) else emptyList()
        } catch (e: RestClientException) {
            log.error("Error while getting user data from user service: ${e.localizedMessage}")
            return
        }

        advertisements.forEach { advertisement ->
            advertisement.notified = true
            val bid = bids.find { bid -> bid.advertisement.id == advertisement.id } ?: return@forEach
            val user = users.find { it.id == bid.userId }

            if (user == null) {
                log.error("User not found with id ${bid.userId} for advertisement ${advertisement.id}")
                return@forEach
            }

            sendEmailNotification(advertisement, user)
            sendPushNotification(advertisement, bid)
        }

        advertisementRepository.saveAll(advertisements)
        log.info("notifying winners finished")
    }

    private fun sendEmailNotification(advertisement: Advertisement, user: UserDataResponse) {
        val mainMapper = ObjectMapper()
        val mainNode = mainMapper.createObjectNode()

        mainNode.put("toAddress", user.email)
        mainNode.put("template", "winnerBid")

        val dataMapper = ObjectMapper()
        val dataNode = dataMapper.createObjectNode()

        dataNode.put("advertisementId", advertisement.id)
        dataNode.put("advertisementTitle", advertisement.title)
        dataNode.put("username", user.username)
        mainNode.set<ObjectNode>("data", dataNode)

        rabbitTemplate.convertAndSend(emailQueue.name, mainNode.toString())
    }

    private fun sendPushNotification(advertisement: Advertisement, bid: Bid) {
        val mainMapper = ObjectMapper()
        val mainNode = mainMapper.createObjectNode()

        mainNode.put("userId", bid.userId)
        mainNode.put("template", "winnerBid")

        val dataMapper = ObjectMapper()
        val dataNode = dataMapper.createObjectNode()

        dataNode.put("advertisementTitle", advertisement.title)
        mainNode.set<ObjectNode>("data", dataNode)

        rabbitTemplate.convertAndSend(pushQueue.name, mainNode.toString())
    }
}
package hu.bme.aut.classifiedadvertisementsite.bidservice.websocket

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.aut.classifiedadvertisementsite.bidservice.security.util.AuthHeaderParser
import hu.bme.aut.classifiedadvertisementsite.bidservice.service.BidService
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class SocketHandler(
    private val bidService: BidService,
) : TextWebSocketHandler() {
    companion object {
        private const val USER_ID = "userId"
        private const val TYPE = "type"
        private const val USER_DATA_HEADER = "x-user-data"
        private const val SUBSCRIBE = "subscribe"
        private const val BID = "bid"
        private const val ADVERTISEMENT_ID = "advertisementId"
        private const val PRICE = "price"
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        super.afterConnectionEstablished(session)
        val userId = AuthHeaderParser.getUserIdFromHeader(session.handshakeHeaders[USER_DATA_HEADER]?.get(0))
        if (userId != null) {
            session.attributes[USER_ID] = userId
        }
    }

    override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {
        super.handleMessage(session, message)
        parseMessage(session, message.payload.toString())
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        super.afterConnectionClosed(session, status)
        bidService.removeSession(session)
    }

    private fun parseMessage(session: WebSocketSession, message: String) {
        val mapper = ObjectMapper()
        val node: JsonNode = mapper.readTree(message)
        val type = node[TYPE].asText()

        when (type) {
            SUBSCRIBE -> {
                bidService.subscribeForBids(session, node[ADVERTISEMENT_ID].asInt())
            }
            BID -> {
                if (session.attributes.containsKey(USER_ID).not()) {
                    return
                }

                bidService.createBid(session.attributes[USER_ID] as Int, node[ADVERTISEMENT_ID].asInt(), node[PRICE].asDouble())
            }
        }
    }
}
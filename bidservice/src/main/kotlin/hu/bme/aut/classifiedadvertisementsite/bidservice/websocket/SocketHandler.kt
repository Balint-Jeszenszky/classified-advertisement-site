package hu.bme.aut.classifiedadvertisementsite.bidservice.websocket

import hu.bme.aut.classifiedadvertisementsite.bidservice.security.util.AuthHeaderParser
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class SocketHandler : TextWebSocketHandler() {
    private val sessions: MutableList<WebSocketSession> = mutableListOf()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val userId = AuthHeaderParser.getUserIdFromHeader(session.handshakeHeaders["x-user-data"]?.get(0))
        sessions.add(session)
        session.attributes["userId"] = userId
        super.afterConnectionEstablished(session)
    }

    override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {
        println("Message from userId(${session.attributes["userId"]}): ${message.payload}")
        super.handleMessage(session, message)
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessions.remove(session)
        super.afterConnectionClosed(session, status)
    }
}
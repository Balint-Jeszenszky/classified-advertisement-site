package hu.bme.aut.classifiedadvertisementsite.bidservice.websocket

import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class SocketHandler : TextWebSocketHandler() {
    private val sessions: MutableList<WebSocketSession> = mutableListOf()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessions.add(session)
        super.afterConnectionEstablished(session)
    }

    override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {
        println(message)
        super.handleMessage(session, message)
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessions.remove(session)
        super.afterConnectionClosed(session, status)
    }
}
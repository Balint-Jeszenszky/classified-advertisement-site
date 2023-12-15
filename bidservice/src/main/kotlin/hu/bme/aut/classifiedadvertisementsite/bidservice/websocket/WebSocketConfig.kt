package hu.bme.aut.classifiedadvertisementsite.bidservice.websocket

import hu.bme.aut.classifiedadvertisementsite.bidservice.service.BidService
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val bidService: BidService,
) : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(SocketHandler(bidService), "/external/live-bids")
    }
}
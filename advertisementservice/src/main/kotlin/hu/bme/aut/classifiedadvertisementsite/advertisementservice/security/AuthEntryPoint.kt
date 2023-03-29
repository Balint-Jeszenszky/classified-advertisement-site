package hu.bme.aut.classifiedadvertisementsite.advertisementservice.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class AuthEntryPoint : AuthenticationEntryPoint {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun commence(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authException: AuthenticationException?
    ) {
        log.error("Unauthorized error: {}", authException?.message)
        response?.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized")
    }
}
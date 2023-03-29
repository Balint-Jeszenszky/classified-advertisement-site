package hu.bme.aut.classifiedadvertisementsite.advertisementservice.security

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*
import kotlin.collections.ArrayList

class AuthHeaderFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("x-user-data")

        if (authHeader != null) {
            val mapper = ObjectMapper()
            val node: JsonNode
            try {
                node = mapper.readTree(Base64.getDecoder().decode(authHeader))
                val userDetails: UserDetails = UserDetailsImpl(
                    node["id"].asInt(),
                    node["username"].asText(),
                    node["email"].asText(),
                    true,
                    jsonArrayToList(node["roles"])
                )
                val authentication =
                    UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            } catch (e: Exception) {
                TODO("logging")
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun jsonArrayToList(node: JsonNode): List<String> {
        val list: MutableList<String> = ArrayList()
        if (node.isArray) {
            for (objNode in node) {
                list.add(objNode.asText())
            }
        }
        return list
    }
}
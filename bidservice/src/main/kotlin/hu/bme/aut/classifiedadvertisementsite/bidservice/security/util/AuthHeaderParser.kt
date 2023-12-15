package hu.bme.aut.classifiedadvertisementsite.bidservice.security.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

class AuthHeaderParser {
    companion object {
        fun getUserIdFromHeader(authHeader: String?): Int? {
            if (authHeader == null) {
                return null
            }

            val mapper = ObjectMapper()
            val node: JsonNode

            return try {
                node = mapper.readTree(Base64.getDecoder().decode(authHeader))
                node["id"].asInt()
            } catch (e: Exception) {
                null
            }
        }
    }
}
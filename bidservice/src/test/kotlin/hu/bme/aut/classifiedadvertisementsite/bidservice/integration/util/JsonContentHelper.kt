package hu.bme.aut.classifiedadvertisementsite.bidservice.integration.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule

class JsonContentHelper {
    companion object {
        fun asJsonString(obj: Any?): String {
            return try {
                val mapper = ObjectMapper()
                mapper.registerModule(JavaTimeModule())
                mapper.writeValueAsString(obj)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }
}
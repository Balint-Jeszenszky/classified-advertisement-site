package hu.bme.aut.classifiedadvertisementsite.advertisementservice.integration.util

import com.fasterxml.jackson.databind.ObjectMapper

class JsonContentHelper {
    companion object {
        fun asJsonString(obj: Any?): String {
            return try {
                val mapper = ObjectMapper()
                mapper.writeValueAsString(obj)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }
}
package hu.bme.aut.classifiedadvertisementsite.advertisementservice.integration.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*


class AuthHelper {
    companion object {
        fun getAdminAuthHeader(id: Int, username: String, email: String): String {
            val mapper = ObjectMapper()
            val node = mapper.createObjectNode()

            node.put("id", id)
            node.put("username", username)
            node.put("email", email)
            val arrayNode = mapper.createArrayNode()
            arrayNode.add("ROLE_ADMIN")
            arrayNode.add("ROLE_USER")
            node.set<JsonNode>("roles", arrayNode)

            return Base64.getEncoder().encodeToString(node.toString().encodeToByteArray())
        }

        fun getUserAuthHeader(id: Int, username: String, email: String): String {
            val mapper = ObjectMapper()
            val node = mapper.createObjectNode()

            node.put("id", id)
            node.put("username", username)
            node.put("email", email)
            val arrayNode = mapper.createArrayNode()
            arrayNode.add("ROLE_USER")
            node.set<JsonNode>("roles", arrayNode)

            return Base64.getEncoder().encodeToString(node.toString().encodeToByteArray())
        }
    }
}
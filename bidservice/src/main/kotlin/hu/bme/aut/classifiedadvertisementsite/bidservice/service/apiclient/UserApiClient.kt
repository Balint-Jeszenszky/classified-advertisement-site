package hu.bme.aut.classifiedadvertisementsite.bidservice.service.apiclient

import hu.bme.aut.classifiedadvertisementsite.bidservice.client.java.api.UsersApi
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class UserApiClient(
    @Value("\${apiclient.userapi.url}") private val userApiUrl: String,
) : UsersApi() {
    init {
        apiClient.basePath = "$userApiUrl/internal"
    }
}
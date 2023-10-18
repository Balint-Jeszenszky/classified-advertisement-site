package hu.bme.aut.classifiedadvertisementsite.advertisementservice.service.apiclient

import hu.bme.aut.classifiedadvertisementsite.advertisementservice.client.java.api.BidApi
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class BidApiClient(
    @Value("\${apiclient.bidapi.url}") private val bidApiUrl: String,
) : BidApi() {
    init {
        apiClient.basePath = "$bidApiUrl/internal"
    }
}
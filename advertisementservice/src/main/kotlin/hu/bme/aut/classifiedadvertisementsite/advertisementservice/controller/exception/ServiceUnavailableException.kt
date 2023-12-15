package hu.bme.aut.classifiedadvertisementsite.advertisementservice.controller.exception

import org.springframework.http.HttpStatus

class ServiceUnavailableException(errorMessage: String) : AbstractException(errorMessage) {
    override fun getHttpStatus(): HttpStatus {
        return HttpStatus.SERVICE_UNAVAILABLE
    }
}

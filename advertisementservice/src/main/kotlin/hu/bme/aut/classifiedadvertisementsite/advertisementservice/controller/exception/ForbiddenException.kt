package hu.bme.aut.classifiedadvertisementsite.advertisementservice.controller.exception

import org.springframework.http.HttpStatus

class ForbiddenException(errorMessage: String) : AbstractException(errorMessage) {
    override fun getHttpStatus(): HttpStatus {
        return HttpStatus.FORBIDDEN
    }
}
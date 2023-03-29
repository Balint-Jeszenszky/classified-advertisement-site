package hu.bme.aut.classifiedadvertisementsite.advertisementservice.controller.exception

import org.springframework.http.HttpStatus

class InternalServerErrorException(errorMessage: String) : AbstractException(errorMessage) {
    override fun getHttpStatus(): HttpStatus {
        return HttpStatus.INTERNAL_SERVER_ERROR
    }
}
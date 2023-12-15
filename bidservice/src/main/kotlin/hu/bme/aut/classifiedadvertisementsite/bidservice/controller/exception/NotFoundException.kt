package hu.bme.aut.classifiedadvertisementsite.bidservice.controller.exception

import org.springframework.http.HttpStatus

class NotFoundException(errorMessage: String) : AbstractException(errorMessage) {
    override fun getHttpStatus(): HttpStatus {
        return HttpStatus.NOT_FOUND
    }
}
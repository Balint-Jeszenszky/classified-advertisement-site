package hu.bme.aut.classifiedadvertisementsite.bidservice.controller.exception

import org.springframework.http.HttpStatus

class BadRequestException(errorMessage: String) : AbstractException(errorMessage) {
    override fun getHttpStatus(): HttpStatus {
        return HttpStatus.BAD_REQUEST
    }
}
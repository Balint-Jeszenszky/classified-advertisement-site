package hu.bme.aut.classifiedadvertisementsite.bidservice.controller.exception

import org.springframework.http.HttpStatus

abstract class AbstractException(errorMessage: String) : RuntimeException(errorMessage) {
    abstract fun getHttpStatus(): HttpStatus
}
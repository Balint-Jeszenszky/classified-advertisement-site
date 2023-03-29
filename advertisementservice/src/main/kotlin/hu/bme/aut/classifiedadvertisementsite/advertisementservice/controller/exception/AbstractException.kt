package hu.bme.aut.classifiedadvertisementsite.advertisementservice.controller.exception

import org.springframework.http.HttpStatus

abstract class AbstractException(errorMessage: String) : RuntimeException(errorMessage) {
    abstract fun getHttpStatus(): HttpStatus
}
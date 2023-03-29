package hu.bme.aut.classifiedadvertisementsite.advertisementservice.controller

import hu.bme.aut.classifiedadvertisementsite.advertisementservice.controller.exception.AbstractException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler


@ControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(AbstractException::class)
    fun handleExceptions(e: AbstractException): ResponseEntity<String?>? {
        return ResponseEntity(e.message, e.getHttpStatus())
    }

    @ExceptionHandler(Exception::class)
    fun handleUnknownExceptions(e: Exception): ResponseEntity<String?>? {
        log.error("Unknown exception handled: {}: {}", e.message, e.stackTrace)
        return ResponseEntity(HttpStatus.FORBIDDEN)
    }
}
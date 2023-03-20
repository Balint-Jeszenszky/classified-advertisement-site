package hu.bme.aut.classifiedadvertisementsite.gateway.controller;

import hu.bme.aut.classifiedadvertisementsite.gateway.controller.exception.AbstractException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AbstractException.class)
    public Mono<ResponseEntity<String>> handleExceptions(AbstractException e) {
        return Mono.just(new ResponseEntity<>(e.getMessage(), e.getHttpStatus()));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<String>> handleUnknownExceptions(Exception e) {
        log.error("Unknown exception handled: {}: {}", e.getMessage(), e.getStackTrace());
        return Mono.just(new ResponseEntity<>(HttpStatus.FORBIDDEN));
    }
}
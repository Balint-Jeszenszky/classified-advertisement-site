package hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.controller;

import hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.controller.exceptions.AbstractException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AbstractException.class)
    public ResponseEntity<String> handleExceptions(AbstractException e) {
        return new ResponseEntity<>(e.getMessage(), e.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleUnknownExceptions(Exception e) {
        log.error("Unknown exception handled: {}: {}", e.getMessage(), e.getStackTrace());
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}
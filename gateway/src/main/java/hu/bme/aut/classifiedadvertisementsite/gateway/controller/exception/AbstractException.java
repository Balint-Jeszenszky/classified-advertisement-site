package hu.bme.aut.classifiedadvertisementsite.gateway.controller.exception;

import org.springframework.http.HttpStatus;

public abstract class AbstractException extends RuntimeException {

    protected AbstractException(String errorMessage) {
        super(errorMessage);
    }

    public abstract HttpStatus getHttpStatus();
}
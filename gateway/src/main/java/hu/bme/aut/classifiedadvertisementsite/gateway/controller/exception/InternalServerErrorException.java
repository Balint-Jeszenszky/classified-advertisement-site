package hu.bme.aut.classifiedadvertisementsite.gateway.controller.exception;

import org.springframework.http.HttpStatus;

public class InternalServerErrorException extends AbstractException {
    public InternalServerErrorException(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
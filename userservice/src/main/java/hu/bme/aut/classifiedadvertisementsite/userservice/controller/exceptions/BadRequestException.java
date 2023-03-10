package hu.bme.aut.classifiedadvertisementsite.userservice.controller.exceptions;

import org.springframework.http.HttpStatus;

public class BadRequestException extends AbstractException {

    public BadRequestException(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
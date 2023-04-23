package hu.bme.aut.classifiedadvertisementsite.imageprocessingservice.controller.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends AbstractException {

    public NotFoundException(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
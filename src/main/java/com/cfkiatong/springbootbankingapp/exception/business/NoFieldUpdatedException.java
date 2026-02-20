package com.cfkiatong.springbootbankingapp.exception.business;

import org.springframework.http.HttpStatus;

public class NoFieldUpdatedException extends BusinessException {

    public NoFieldUpdatedException() {
        super("At least one field must be updated");
    }

    public HttpStatus getStatus() {
        return HttpStatus.UNPROCESSABLE_CONTENT;
    }

}

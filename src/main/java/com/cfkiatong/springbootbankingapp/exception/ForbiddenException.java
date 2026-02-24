package com.cfkiatong.springbootbankingapp.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException() {
        super("You are not authorized to perform this transaction");
    }

    public HttpStatus getStatus() {
        return HttpStatus.FORBIDDEN;
    }
}

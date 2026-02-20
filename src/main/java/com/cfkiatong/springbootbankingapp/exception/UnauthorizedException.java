package com.cfkiatong.springbootbankingapp.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("You are not authorized to perform this operation");
    }

    public HttpStatus getStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}

package com.cfkiatong.springbootbankingapp.exception.business;

import org.springframework.http.HttpStatus;

public class NoTargetAccountException extends BusinessException {

    public NoTargetAccountException() {
        super("Must have a target account for this transaction");
    }

    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }

}

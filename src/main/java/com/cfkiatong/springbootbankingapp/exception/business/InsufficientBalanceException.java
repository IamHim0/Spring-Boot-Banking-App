package com.cfkiatong.springbootbankingapp.exception.business;

import org.springframework.http.HttpStatus;

public class InsufficientBalanceException extends BusinessException {

    public InsufficientBalanceException() {
        super("Your current balance is insufficient for this transaction");
    }

    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }

}

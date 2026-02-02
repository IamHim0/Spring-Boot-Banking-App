package com.cfkiatong.springbootbankingapp.exception.business;

public class InsufficientBalanceException extends BusinessException {
    public InsufficientBalanceException() {
        super("Your current balance is insufficient for this transaction");
    }
}

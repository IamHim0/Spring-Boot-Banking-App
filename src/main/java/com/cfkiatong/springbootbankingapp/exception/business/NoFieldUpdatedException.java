package com.cfkiatong.springbootbankingapp.exception.business;

public class NoFieldUpdatedException extends BusinessException {
    public NoFieldUpdatedException() {
        super("At least one field must be updated");
    }
}

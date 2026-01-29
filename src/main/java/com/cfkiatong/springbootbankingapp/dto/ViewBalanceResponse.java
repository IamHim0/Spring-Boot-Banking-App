package com.cfkiatong.springbootbankingapp.dto;

import java.math.BigDecimal;

public class ViewBalanceResponse {

    private BigDecimal balance;

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    
}

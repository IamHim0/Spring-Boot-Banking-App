package com.cfkiatong.springbootbankingapp.dto;

import java.math.BigDecimal;

public class WithdrawRequest {
    private BigDecimal withdrawal;

    public BigDecimal getWithdrawal() {
        return withdrawal;
    }

    public void setWithdrawal(BigDecimal withdrawal) {
        this.withdrawal = withdrawal;
    }
}
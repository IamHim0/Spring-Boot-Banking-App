package com.cfkiatong.springbootbankingapp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class WithdrawRequest {

    @NotNull(message = "withdrawal amount cannot be empty")
    @DecimalMin(value = "0.1", message = "withdrawal must be at least ₱ 0.1 ")
    private BigDecimal withdrawal;

    public BigDecimal getWithdrawal() {
        return withdrawal;
    }

    public void setWithdrawal(BigDecimal withdrawal) {
        this.withdrawal = withdrawal;
    }

}
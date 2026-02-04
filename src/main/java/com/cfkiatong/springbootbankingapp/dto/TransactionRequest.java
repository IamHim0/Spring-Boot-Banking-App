package com.cfkiatong.springbootbankingapp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class TransactionRequest {

    @NotNull(message = "transaction type cannot be empty")
    private TransactionType type;

    @NotNull(message = "transaction amount cannot be empty")
    @DecimalMin(value = "100", message = "transaction amount must be at least ₱ 100")
    private BigDecimal amount;

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
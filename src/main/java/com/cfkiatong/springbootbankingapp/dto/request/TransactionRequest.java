package com.cfkiatong.springbootbankingapp.dto.request;

import com.cfkiatong.springbootbankingapp.dto.TransactionType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionRequest(
        @NotNull(message = "transaction type cannot be empty")
        TransactionType type,
        @NotNull(message = "transaction amount cannot be empty")
        @DecimalMin(value = "100", message = "transaction amount must be at least ₱ 100")
        @DecimalMax(value = "10000", message = "cannot make transactions worth over ₱ 10,000")
        BigDecimal amount,
        UUID targetAccountId) {
}
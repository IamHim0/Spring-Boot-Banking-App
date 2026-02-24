package com.cfkiatong.springbootbankingapp.dto;

import com.cfkiatong.springbootbankingapp.entity.Transaction;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionDTO {

    private final LocalDateTime timestamp;
    private final UUID transactionId;
    private final TransactionType type;
    private final UUID sourceAccount;
    private final UUID targetAccount;
    private final BigDecimal transactionAmount;
    private final BigDecimal sourceBalanceBefore;
    private final BigDecimal sourceBalanceAfter;

    public TransactionDTO(Transaction transaction) {
        this.timestamp = transaction.getTimestamp();
        this.transactionId = transaction.getTransactionId();
        this.type = transaction.getType();
        this.sourceAccount = transaction.getSourceAccount();
        this.targetAccount = transaction.getTargetAccount();
        this.transactionAmount = transaction.getTransactionAmount();
        this.sourceBalanceBefore = transaction.getSourceBalanceBefore();
        this.sourceBalanceAfter = transaction.getSourceBalanceAfter();
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public TransactionType getType() {
        return type;
    }

    public UUID getSourceAccount() {
        return sourceAccount;
    }

    public UUID getTargetAccount() {
        return targetAccount;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public BigDecimal getSourceBalanceBefore() {
        return sourceBalanceBefore;
    }

    public BigDecimal getSourceBalanceAfter() {
        return sourceBalanceAfter;
    }
}
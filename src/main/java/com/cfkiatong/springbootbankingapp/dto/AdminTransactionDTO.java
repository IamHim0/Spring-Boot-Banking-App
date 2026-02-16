package com.cfkiatong.springbootbankingapp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class AdminTransactionDTO {

    private LocalDateTime timestamp;
    private UUID transactionId;
    private TransactionType type;
    private UUID sourceAccount;
    private UUID targetAccount;
    private BigDecimal transactionAmount;
    private BigDecimal sourceBalanceBefore;
    private BigDecimal sourceBalanceAfter;
    private BigDecimal targetBalanceBefore;
    private BigDecimal targetBalanceAfter;

    public AdminTransactionDTO(LocalDateTime timestamp, UUID transactionId, TransactionType type, UUID sourceAccount, UUID targetAccount, BigDecimal transactionAmount, BigDecimal sourceBalanceBefore, BigDecimal sourceBalanceAfter, BigDecimal targetBalanceBefore, BigDecimal targetBalanceAfter) {
        this.timestamp = timestamp;
        this.transactionId = transactionId;
        this.type = type;
        this.sourceAccount = sourceAccount;
        this.targetAccount = targetAccount;
        this.transactionAmount = transactionAmount;
        this.sourceBalanceBefore = sourceBalanceBefore;
        this.sourceBalanceAfter = sourceBalanceAfter;
        this.targetBalanceBefore = targetBalanceBefore;
        this.targetBalanceAfter = targetBalanceAfter;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public UUID getSourceAccount() {
        return sourceAccount;
    }

    public void setSourceAccount(UUID sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    public UUID getTargetAccount() {
        return targetAccount;
    }

    public void setTargetAccount(UUID targetAccount) {
        this.targetAccount = targetAccount;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public BigDecimal getSourceBalanceBefore() {
        return sourceBalanceBefore;
    }

    public void setSourceBalanceBefore(BigDecimal sourceBalanceBefore) {
        this.sourceBalanceBefore = sourceBalanceBefore;
    }

    public BigDecimal getSourceBalanceAfter() {
        return sourceBalanceAfter;
    }

    public void setSourceBalanceAfter(BigDecimal sourceBalanceAfter) {
        this.sourceBalanceAfter = sourceBalanceAfter;
    }

    public BigDecimal getTargetBalanceBefore() {
        return targetBalanceBefore;
    }

    public void setTargetBalanceBefore(BigDecimal targetBalanceBefore) {
        this.targetBalanceBefore = targetBalanceBefore;
    }

    public BigDecimal getTargetBalanceAfter() {
        return targetBalanceAfter;
    }

    public void setTargetBalanceAfter(BigDecimal targetBalanceAfter) {
        this.targetBalanceAfter = targetBalanceAfter;
    }

}

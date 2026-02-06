package com.cfkiatong.springbootbankingapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID transactionId;
    private UUID sourceAccount;
    private UUID targetAccount;
    private BigDecimal transactionAmount;
    private BigDecimal sourceBalanceBefore;
    private BigDecimal sourceBalanceAfter;
    private BigDecimal targetBalanceBefore;
    private BigDecimal targetBalanceAfter;

    public Transaction() {

    }

    public Transaction(UUID sourceAccount, BigDecimal transactionAmount, UUID targetAccount, BigDecimal sourceBalanceBefore, BigDecimal sourceBalanceAfter) {
        this.sourceAccount = sourceAccount;
        this.transactionAmount = transactionAmount;
        this.targetAccount = targetAccount;
        this.sourceBalanceBefore = sourceBalanceBefore;
        this.sourceBalanceAfter = sourceBalanceAfter;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public UUID getTargetAccount() {
        return targetAccount;
    }

    public void setTargetAccount(UUID targetAccount) {
        this.targetAccount = targetAccount;
    }

    public UUID getSourceAccount() {
        return sourceAccount;
    }

    public void setSourceAccount(UUID sourceAccount) {
        this.sourceAccount = sourceAccount;
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

    public void setTargetBalanceAfter(BigDecimal getTargetBalanceAfter) {
        this.targetBalanceAfter = getTargetBalanceAfter;
    }
}

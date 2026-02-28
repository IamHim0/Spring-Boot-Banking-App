package com.cfkiatong.springbootbankingapp.dto.response;

import com.cfkiatong.springbootbankingapp.dto.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(LocalDateTime timestamp, UUID transactionId, TransactionType type, UUID sourceAccount,
                                  UUID targetAccount, BigDecimal transactionAmount, BigDecimal sourceBalanceBefore,
                                  BigDecimal sourceBalanceAfter) {

}